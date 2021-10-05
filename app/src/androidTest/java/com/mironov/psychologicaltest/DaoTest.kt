package com.mironov.psychologicaltest

import androidx.annotation.Nullable
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mironov.psychologicaltest.data.QuestionDatabase
import com.mironov.psychologicaltest.model.Question
import com.mironov.psychologicaltest.repository.QuestionRepository
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DaoTest {

    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun getAllQuestions() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val repository: QuestionRepository

        val questionDao = QuestionDatabase.getDatabase(
            appContext
        ).questionDao()
        repository = QuestionRepository(questionDao)

        repository.getRowsCount("azenk_child").observeForever(object : Observer<Int?> {
            override fun onChanged(@Nullable sections: Int?) {
                assertEquals(sections, 60)
            }
        })
    }

    @Test
    fun getQuestionById() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val readAllData: LiveData<Question?>
        val repository: QuestionRepository

        val questionDao = QuestionDatabase.getDatabase(
            appContext
        ).questionDao()
        repository = QuestionRepository(questionDao)
        readAllData = repository.getQuestionById("azenk_child",1)

        readAllData.observeForever(object : Observer<Question?> {
            override fun onChanged(q: Question?) {
                assertEquals(q?.questionText, "Любишь ли ты шум и суету вокруг себя")
            }
        })
    }
}