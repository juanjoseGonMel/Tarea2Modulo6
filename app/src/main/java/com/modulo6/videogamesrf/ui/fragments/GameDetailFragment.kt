package com.modulo6.videogamesrf.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.text.LineBreaker
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.modulo6.videogamesrf.R
import com.modulo6.videogamesrf.application.VideoGamesRFApp
import com.modulo6.videogamesrf.data.GameRepository
import com.modulo6.videogamesrf.data.remote.model.GameDetailDto
import com.modulo6.videogamesrf.databinding.FragmentGameDetailBinding
import com.modulo6.videogamesrf.util.Constants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val GAME_ID = "game_id"

class GameDetailFragment : Fragment(), OnMapReadyCallback {

    private var gameId: String? = null

    private var _binding: FragmentGameDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: GameRepository
    private lateinit var mediaPlayer: MediaPlayer


    //Propiedad global para el mapa
    private lateinit var map: GoogleMap
    private lateinit var locationManager: LocationManager

    //Para el permiso de la localizacion
    private var fineLocationPermissionGranted = false

    //Latitud y longitud
    private var lati:Double = 0.0
    private var longi:Double = 0.0


    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted){
            //Se concedió el permiso
            actionPermissionGranted()
        }else{
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.permiso))
                    .setMessage(getString(R.string.permisoMsg))
                    .setPositiveButton(getString(R.string.btnEntendido)){ _, _ ->
                        updateOrRequestPermissions()
                    }
                    .setNegativeButton(getString(R.string.btnSalir)){ dialog, _ ->
                        dialog.dismiss()
                        requireActivity().finish()
                    }
                    .create()
                    .show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.PermisoNegadoPermanente),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gameId = it.getString(GAME_ID)
            Log.d(Constants.LOGTAG,"id recibido $gameId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = (requireActivity().application as VideoGamesRFApp).repository
        binding.btnRetry.setOnClickListener { loadGameDetails() }
        loadGameDetails()
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.accumula_town)
        mediaPlayer.start()


        //Mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        binding.vvVideo.release()

        //locationManager.removeUpdates(this)

        _binding = null
    }




    //Funciones normales
    private fun loadGameDetails() {
        binding.pbLoading.visibility = View.VISIBLE
        binding.tvErrorMessage.visibility = View.GONE
        binding.btnRetry.visibility = View.GONE

        gameId?.let { id ->
            val call: Call<GameDetailDto> = repository.getGamesDetail(id)
            call.enqueue(object : Callback<GameDetailDto> {
                @SuppressLint("StringFormatInvalid")
                override fun onResponse(p0: Call<GameDetailDto>, response: Response<GameDetailDto>) {
                    binding.pbLoading.visibility = View.GONE

                    if (response.isSuccessful) {
                        response.body()?.let { gameDetail ->
                            binding.tvTitle.text = gameDetail.nombre
                            binding.tvLongDesc.text = gameDetail.descripcion
                            binding.tvMov.text = gameDetail.movimientos
                            binding.tvType.text = gameDetail.tipo
                            binding.tvHuevo.text = gameDetail.grupoHuevo
                            binding.tvHabilidad.text = gameDetail.habilidades
                            binding.tvGeneracion.text = gameDetail.generacion.toString()

                            //Asignamos latitud y longitud
                            lati = gameDetail.latitud
                            longi = gameDetail.longitud

                            binding.vvVideo.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.loadVideo(gameDetail.urlVideo, 0f)
                                }
                            })
                            lifecycle.addObserver(binding.vvVideo)

                            Glide.with(binding.root.context)
                                .load(gameDetail.urlImagen)
                                .into(binding.ivImage)

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                binding.tvLongDesc.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
                        } ?: showError(getString(R.string.error_no_data))
                    } else {
                        showError(getString(R.string.error_server))
                    }
                }

                override fun onFailure(p0: Call<GameDetailDto>, p1: Throwable) {
                    binding.pbLoading.visibility = View.GONE
                    showError(getString(R.string.error_network))
                }
            })
        }
    }

    private fun showError(message: String) {
        binding.tvErrorMessage.text = message
        binding.tvErrorMessage.visibility = View.VISIBLE
        binding.btnRetry.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance(gameId: String) =
            GameDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(GAME_ID, gameId)
                }
            }
    }
    //Normales



    //Funciones mapa

    override fun onResume() {
        super.onResume()
        if (!::map.isInitialized) return
        if (!fineLocationPermissionGranted){
            updateOrRequestPermissions()
        }
    }


    @SuppressLint("MissingPermission")
    private fun actionPermissionGranted(){
        map.isMyLocationEnabled = true
        /*
        val locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000,
            10F,
            this
        )

         */
    }



    private fun updateOrRequestPermissions() {
        //Revisando el permiso
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        fineLocationPermissionGranted = hasFineLocationPermission

        if (!fineLocationPermissionGranted) {
            //Pedimos el permiso
            permissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            //Tenemos los permisos
            actionPermissionGranted()
        }

    }

    private fun createMarker(){
        val coordinates = LatLng(lati,longi)
        val marker = MarkerOptions()
            .position(coordinates)
            .title(getString(R.string.PokemonAvistado))
            .snippet(getString(R.string.UbicacionPokemon))
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconpoke))

        map.addMarker(marker)

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 18f),
            4000,
            null
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //Obtenemos un objeto con alcance global del mapa
        map = googleMap
        createMarker()
        updateOrRequestPermissions()
    }


    /*
    override fun onLocationChanged(location: Location) {

        map.clear()
        val coordinates = LatLng(location.latitude, location.longitude)
        val marker = MarkerOptions()
            .position(coordinates)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.person))

        map.addMarker(marker)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 18f))
    }

     */


}