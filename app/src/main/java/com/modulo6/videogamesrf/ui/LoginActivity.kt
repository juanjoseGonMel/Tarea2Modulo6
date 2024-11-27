package com.modulo6.videogamesrf.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.modulo6.videogamesrf.R
import com.modulo6.videogamesrf.databinding.ActivityLoginBinding
import com.modulo6.videogamesrf.message

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    //Propiedad para firebase auth
    private lateinit var firebaseAuth : FirebaseAuth

    //Propiedades para el usuario y contraseña
    private var email = ""
    private var contrasenia = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //Instanciamos el objecto firebase
        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null)
            actionLoginSuccessful()

        binding.btnLogin.setOnClickListener {

            if (!validateFields()) return@setOnClickListener
            binding.progressBar.visibility = View.VISIBLE
            authenticateUser(email, contrasenia)
        }

        binding.btnRegistrarse.setOnClickListener {

            if (!validateFields()) return@setOnClickListener
            binding.progressBar.visibility = View.VISIBLE

            firebaseAuth.createUserWithEmailAndPassword(email, contrasenia)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful){
                        message(getString(R.string.userSussccess))
                        //Enviamos un correo para verificar la direcion de email
                        firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                            message(getString(R.string.correoVerificacion))
                        }?.addOnFailureListener{
                            message(getString(R.string.correoVerificacionNoEnviado))
                        }
                        actionLoginSuccessful()
                    }else{
                        binding.progressBar.visibility = View.GONE
                        handleErrors(authResult)
                    }
                }
        }

        binding.tvRestablecerPassword.setOnClickListener{
            val resetMail = EditText(this)
            resetMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

            AlertDialog.Builder(this)
                .setTitle("Restablecer contraseña")
                .setMessage("Ingresa su correo electronico para recibir el enlace de restablecimiento")
                .setView(resetMail)
                .setPositiveButton("Enviar"){ _, _ ->
                    val mail = resetMail.text.toString()
                    if(mail.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                        //Mandamos el enlace de restablecimiento
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener {
                            message("El correo para restablecer la contraseña ha sido enviado")
                        }.addOnFailureListener {
                            message("El enlace no se ha podido enviar")
                        }
                    }else{
                        message("Favor de ingresar una dirección de correo válida")
                    }
                }
                .setNegativeButton("Cancelar"){ dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }


    }

    private fun validateFields(): Boolean{
        email = binding.tietEmail.text.toString().trim()  //Elimina los espacios en blanco
        contrasenia = binding.tietContrasenia.text.toString().trim()

        //Verifica que el campo de correo no esté vacío
        if(email.isEmpty()){
            binding.tietEmail.error = getString(R.string.msgErrornoEmail)
            binding.tietEmail.requestFocus()
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tietEmail.error = getString(R.string.msgErrornoValido)
            binding.tietEmail.requestFocus()
            return false
        }

        //Verifica que el campo de la contraseña no esté vacía y tenga al menos 6 caracteres
        if(contrasenia.isEmpty()){
            binding.tietContrasenia.error = getString(R.string.msgErrornoPass)
            binding.tietContrasenia.requestFocus()
            return false
        }else if(contrasenia.length < 6){
            binding.tietContrasenia.error = getString(R.string.WeakPass)
            binding.tietContrasenia.requestFocus()
            return false
        }
        return true
    }

    private fun handleErrors(task: Task<AuthResult>){
        var errorCode = ""

        try{
            errorCode = (task.exception as FirebaseAuthException).errorCode
        }catch(e: Exception){
            e.printStackTrace()
        }

        when(errorCode){
            "ERROR_INVALID_EMAIL" -> {
                Toast.makeText(this, getString(R.string.InvalidEmail), Toast.LENGTH_SHORT).show()
                binding.tietEmail.error = getString(R.string.InvalidEmail)
                binding.tietEmail.requestFocus()
            }
            "ERROR_WRONG_PASSWORD" -> {
                Toast.makeText(this, getString(R.string.WrongPass), Toast.LENGTH_SHORT).show()
                binding.tietContrasenia.error = getString(R.string.WrongPass)
                binding.tietContrasenia.requestFocus()
                binding.tietContrasenia.setText("")

            }
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                //An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
                Toast.makeText(this, getString(R.string.AcountExist), Toast.LENGTH_SHORT).show()
            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                Toast.makeText(this, getString(R.string.EmailinUse), Toast.LENGTH_LONG).show()
                binding.tietEmail.error = (getString(R.string.EmailinUse))
                binding.tietEmail.requestFocus()
            }
            "ERROR_USER_TOKEN_EXPIRED" -> {
                Toast.makeText(this, getString(R.string.userExpired), Toast.LENGTH_LONG).show()
            }
            "ERROR_USER_NOT_FOUND" -> {
                Toast.makeText(this, getString(R.string.UserNotFound), Toast.LENGTH_LONG).show()
            }
            "ERROR_WEAK_PASSWORD" -> {
                Toast.makeText(this, getString(R.string.WeakPass), Toast.LENGTH_LONG).show()
                binding.tietContrasenia.error = getString(R.string.WeakPass)
                binding.tietContrasenia.requestFocus()
            }
            "NO_NETWORK" -> {
                Toast.makeText(this, getString(R.string.noNet), Toast.LENGTH_LONG).show()
            }
            else -> {
                Toast.makeText(this, getString(R.string.errorNose), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun actionLoginSuccessful(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun authenticateUser(usr : String, psw : String){
        firebaseAuth.signInWithEmailAndPassword(usr, psw).addOnCompleteListener { authResult ->

            //Verificamos si fue exitosa la autentificacion.
            if (authResult.isSuccessful){
                message(getString(R.string.auth_success))
                actionLoginSuccessful()
            }else{
                binding.progressBar.visibility = View.GONE
                handleErrors(authResult)
            }
        }
    }

}