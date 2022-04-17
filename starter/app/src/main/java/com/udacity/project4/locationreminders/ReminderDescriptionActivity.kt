package com.udacity.project4.locationreminders

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityReminderDescriptionBinding
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import org.koin.android.ext.android.inject

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class ReminderDescriptionActivity : AppCompatActivity() {
    private lateinit var geofencingClient: GeofencingClient
    val _viewModel: SaveReminderViewModel by inject()

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminderDataItem: ReminderDataItem): Intent {
            val intent = Intent(context, ReminderDescriptionActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminderDataItem)
            return intent
        }
    }

    private lateinit var binding: ActivityReminderDescriptionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_reminder_description
        )
        geofencingClient = LocationServices.getGeofencingClient(this)
        var reminderItem = intent.getSerializableExtra(EXTRA_ReminderDataItem) as ReminderDataItem

       // removeGeofence(reminderItem.id)
//        TODO: Add the implementation of the reminder details
        binding.reminderDataItem = reminderItem

    }

    private fun removeGeofence(geofenceId: String) {
        geofencingClient.removeGeofences(listOf(geofenceId))?.run {
            addOnSuccessListener { //in case of success removing
                Log.d("GeofenceUtil", getString(R.string.geofence_removed))
                Toast.makeText(
                    applicationContext,
                    getString(R.string.geofence_removed),
                    Toast.LENGTH_SHORT
                ).show()
                _viewModel.deleteReminder(geofenceId)

            }
            addOnFailureListener { ////in case of failure
                Toast.makeText(
                    applicationContext,
                    getString(R.string.geofence_not_removed),
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("GeofenceUtil", getString(R.string.geofence_not_removed))
            }
        }

    }
}
