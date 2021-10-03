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
    var list: List<Question>? = null


    @get:Rule
    var instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun getAllQuestions() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val readAllData: LiveData<List<Question>>
        val repository: QuestionRepository

        val questionDao = QuestionDatabase.getDatabase(
            appContext
        ).questionDao()
        repository = QuestionRepository(questionDao)
        readAllData = repository.readAllData

        readAllData.observeForever(object : Observer<List<Question>?> {
            override fun onChanged(@Nullable sections: List<Question>?) {
                list = readAllData.value
                assertEquals(list?.size, 60)
            }
        })

    }
}