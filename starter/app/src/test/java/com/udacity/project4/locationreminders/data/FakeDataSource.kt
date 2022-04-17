package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source
    private var shouldReturnError = false

    fun setReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        //TODO("Return the reminders")

        if (shouldReturnError){
            return Result.Error("Reminders not found", 404)
        }else{
            reminders?.let { return Result.Success(it) }
            return Result.Error(
                "Location reminder data not found"
            )
        }

       /* reminders?.let { return Result.Success(it) }
        return Result.Error(
            "Location reminder data not found"
        )*/
         //return Result.Success(ArrayList(reminders))

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {

        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {

        if(shouldReturnError){

            return Result.Error("Error in getting Reminder")

        }
        else {
            val reminder = reminders?.find { it.id == id }

            if (reminder != null) {
                return Result.Success(reminder)
            } else {
                return Result.Error("Reminder not found", 404)
            }
        }
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}