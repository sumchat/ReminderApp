package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var mMap: GoogleMap
    private val TAG: String = SelectLocationFragment::class.java.getSimpleName()
    private var isLocationSelected = false
    //private  var pointOfInterest: PointOfInterest? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var poiName: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this


        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location
       // onLocationSelected()

        return binding.root
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableUserLocation()

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16f))
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        setMapLongClick(mMap)
        setMapStyle(mMap)
        mapPoiClick(mMap)
        onLocationSelected()
    }

    private fun setMapLongClick(mMap: GoogleMap) {
        mMap.setOnMapLongClickListener { latLng ->
            mMap.clear()
            val location = getGeocodedAddress(latLng.latitude, latLng.longitude)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng).title(location)
            )

            val zoomLevel = 15f
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
            isLocationSelected = true
            latitude = latLng.latitude
            longitude = latLng.longitude
            poiName = location
            marker.showInfoWindow()
        }
    }

        private fun getGeocodedAddress(LATITUDE: Double, LONGITUDE: Double): String? {
            var strAdd = ""
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
                if (addresses != null) {
                    val returnedAddress: Address = addresses[0]
                    val strReturnedAddress = StringBuilder("")
                    for (i in 0..returnedAddress.maxAddressLineIndex) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                    }
                    strAdd = strReturnedAddress.toString()
                    //Log.v(" location address", strReturnedAddress.toString())
                } else {
                    //Log.v("location address", "No Address returned!")
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                //Log.v("location address", "Canont get Address!")
            }
            return strAdd
        }
    private fun setMapStyle(mMap: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                   R.raw.style_json
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

    }
    private fun mapPoiClick(mMap: GoogleMap) {
       mMap.setOnPoiClickListener { poi ->
           mMap.clear()
           val poiMarker = mMap.addMarker(
               MarkerOptions()
                   .position(poi.latLng)
                   .title(poi.name)
                   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
           )
           val zoomLevel = 15f
           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(poi.latLng, zoomLevel))
           poiMarker.showInfoWindow()
           isLocationSelected = true
           latitude = poi.latLng.latitude
           longitude = poi.latLng.longitude
           //pointOfInterest = poi
           poiName = poi.name

       }
    }


    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
           // getActivity()?.let { ActivityCompat.requestPermissions(it,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE) }
            ActivityCompat.requestPermissions(requireActivity(),arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE)


            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }

        mMap.isMyLocationEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults:IntArray)
    {
        if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // Permission is granted. Continue...
            enableUserLocation()

        } else {
            // Displays App settings screen.
            Snackbar.make(
                binding.root,
                R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.settings) {
                    // Displays App settings screen.
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show() }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && mMap != null)
                    mMap?.isMyLocationEnabled = true

    }





    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        binding.btnContinue.setOnClickListener{


            if (isLocationSelected)
            {
                _viewModel.latitude.value = latitude
                _viewModel.longitude.value = longitude

                _viewModel.reminderSelectedLocationStr.value = poiName
               // _viewModel.selectedPOI.value = pointOfInterest
                _viewModel.navigationCommand.postValue(NavigationCommand.Back)
               // _viewModel.navigationCommand.value = NavigationCommand.To(SelectLocationFragmentDirections.actionSelectLocationFragmentToSaveReminderFragment())
            }else{
                Toast.makeText(context, "Please select a location", Toast.LENGTH_LONG).show()
            }

        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            mMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            mMap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


}
