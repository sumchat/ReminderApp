package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var fakeRepository: FakeDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun createRepository() {
        stopKoin()

        fakeRepository = FakeDataSource()
        reminderListViewModel = RemindersListViewModel(
            getApplicationContext(),
            fakeRepository
        )
    }

    @Test
    fun loadRemindersTest(){
        var reminder1 = ReminderDTO("title1", "description1", "redlands", 34.08357, -117.14382)
        var reminder2 = ReminderDTO("title2", "description2", "redlands", 34.08357, -117.14382)
        var reminder3 = ReminderDTO("title3", "description3", "redlands", 34.08357, -117.14382)
        val remindersList = mutableListOf(reminder1, reminder2, reminder3)
        fakeRepository = FakeDataSource(remindersList!!)
        reminderListViewModel = RemindersListViewModel(
            getApplicationContext(),
            fakeRepository
        )
        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue(), (not(emptyList())))
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue().size, `is`(remindersList.size))
    }

    @Test
    fun showLoading_withdata() =  runBlocking{


        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

       // assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        assertThat(reminderListViewModel.showNoData.getOrAwaitValue(), `is`(true))


    }

    @Test
    fun returnError() {
        fakeRepository = FakeDataSource(null)
        reminderListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeRepository)
        fakeRepository.setReturnError(true)

        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(), `is`("Reminders not found"))
    }



}