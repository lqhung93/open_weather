package com.hung.openweather.utils

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import java.io.IOException
import java.nio.charset.Charset

class UiTestUtils {

    companion object {

        fun sleep(millis: Long) {
            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(millis)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        }

        fun getString(@StringRes id: Int, vararg args: Any): String {
            return ApplicationProvider.getApplicationContext<Context>().getString(id, *args)
        }

        fun <VH : RecyclerView.ViewHolder?> actionOnItemViewAtPosition(
            position: Int,
            @IdRes viewId: Int,
            viewAction: ViewAction
        ): ViewAction? {
            return ActionOnItemViewAtPositionViewAction<VH>(position, viewId, viewAction)
        }

        private class ActionOnItemViewAtPositionViewAction<VH : RecyclerView.ViewHolder?>(
            private val position: Int,
            @param:IdRes private val viewId: Int,
            private val viewAction: ViewAction
        ) : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return CoreMatchers.allOf(
                    arrayListOf(
                        ViewMatchers.isAssignableFrom(RecyclerView::class.java),
                        ViewMatchers.isDisplayed()
                    )
                )
            }

            override fun getDescription(): String {
                return ("actionOnItemAtPosition performing ViewAction: "
                        + viewAction.description
                        + " on item at position: "
                        + position)
            }

            override fun perform(uiController: UiController, view: View) {
                val recyclerView = view as RecyclerView
                ScrollToPositionViewAction(position).perform(uiController, view)
                uiController.loopMainThreadUntilIdle()
                val targetView: View = recyclerView.getChildAt(position).findViewById(viewId)
                if (targetView == null) {
                    throw PerformException.Builder().withActionDescription(this.toString())
                        .withViewDescription(
                            HumanReadables.describe(view)
                        )
                        .withCause(
                            IllegalStateException(
                                "No view with id "
                                        + viewId
                                        + " found at position: "
                                        + position
                            )
                        )
                        .build()
                } else {
                    viewAction.perform(uiController, targetView)
                }
            }
        }

        private class ScrollToPositionViewAction(private val position: Int) :
            ViewAction {
            override fun getConstraints(): Matcher<View> {
                return CoreMatchers.allOf(
                    arrayListOf(
                        ViewMatchers.isAssignableFrom(RecyclerView::class.java),
                        ViewMatchers.isDisplayed()
                    )
                )
            }

            override fun getDescription(): String {
                return "scroll RecyclerView to position: $position"
            }

            override fun perform(uiController: UiController?, view: View) {
                val recyclerView = view as RecyclerView
                recyclerView.scrollToPosition(position)
            }
        }


        fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
            return RecyclerViewMatcher(recyclerViewId)
        }

        fun getJsonFromResource(obj: Any, fileName: String): String? {
            return try {
                val classLoader = obj.javaClass.classLoader
                val `is` = classLoader.getResourceAsStream(fileName)
                val size: Int = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()
                String(buffer, Charset.forName("UTF-8"))
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }
    }
}