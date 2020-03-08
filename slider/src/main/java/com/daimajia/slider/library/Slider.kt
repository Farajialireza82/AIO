package com.daimajia.slider.library

import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.schedule
import kotlin.random.Random

@ModelView(saveViewState = true, autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class Slider @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null,
        defStyle: Int = 0
) : Carousel(context, attr, defStyle) {

    private var timer: Timer? = null

    private var timerDelay: Long = 5000

    private val linearLayoutManager: LinearLayoutManager
        get() = layoutManager as LinearLayoutManager

    private val size = AtomicInteger(0)

    private var sliderTask: TimerTask? = null

    private var infinite: Boolean = false
    private val infiniteSize = AtomicInteger(0)
    private var infiniteModels: List<EpoxyModel<*>> = emptyList()

    private var models: List<EpoxyModel<*>> = emptyList()

    init {
        setPadding(Padding.dp(8, 8))
        numViewsToShowOnScreen = 1.1F
    }

    override fun createLayoutManager(): LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        size.set(adapter?.itemCount ?: 0)
        schedule()
    }

    @ModelProp
    @JvmOverloads
    fun cycleDelay(delay: Long = 5000) {
        timerDelay = delay
    }

    @OnViewRecycled
    fun viewRecycled() {
        stopSlider()
    }

    override fun setModels(models: List<EpoxyModel<*>>) {
        this.models = models
        size.set(models.size)
    }

    @ModelProp
    @JvmOverloads
    fun setInfinite(infinite: Boolean = false) {
        this.infinite = infinite
    }

    @AfterPropsSet
    fun setModelsToController() {
        if (infinite && models.size >= 3) {
            infiniteModels = models.toMutableList().apply {
                addAll(models.subList(0, 3).mapIndexed { index, it ->
                    it.id(Random.nextLong())
                })
                addAll(0, models.subList(models.size - 3, models.size)
                        .mapIndexed { index, it ->
                            it.id(Random.nextLong())
                        })
            }
// errors because of the same ids
            infiniteSize.set(infiniteModels.size)
            super.setModels(infiniteModels)
        } else {
            super.setModels(models)
        }

        schedule()
    }

    override fun onDetachedFromWindow() {
        size.set(0)
        stopSlider()
        timer?.cancel()
        super.onDetachedFromWindow()
    }


    private fun schedule() {
        stopSlider()

        if (size.get() == 0) {
            return
        }

        timer = Timer()
        sliderTask = object : TimerTask() {
            override fun run() {
                post {
                    scrollToNextSlide()
                }
            }
        }
        timer?.schedule(sliderTask, timerDelay)
    }

    private fun stopSlider() {
        sliderTask?.cancel()
        sliderTask = null
        timer?.cancel()
        timer = null
    }

    private fun scrollToNextSlide() {
        val position = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        if (size.get() != 0 && position != RecyclerView.NO_POSITION) {
            if (infinite && size.get() >= 3) {
                //loop infinitely
                if (infiniteSize.get() - 2 == position) {
                    scrollToPosition(1)
                } else {
                    smoothScrollToPosition(position + 1)
                }
            } else {
                //normal scrolling
                if (position + 1 < size.get()) {
                    smoothScrollToPosition(position + 1)
                } else {
                    scrollToPosition(0)
                }
            }
        }

        schedule()
    }
}