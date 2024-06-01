package net.proyecto.victorberenguermadrid.musicheads.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import net.proyecto.victorberenguermadrid.musicheads.R

class StartActivity : AppCompatActivity() {
    //permite abrir FirebaseUI esperando resultado de si estamos autenticados
    private val autenticacionLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.resultadoAutenticacion(res)
    }
    //la lista de los proveedores con los que nos podemos autenticar
    val proveedoresAuth = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build()/*,
        AuthUI.IdpConfig.PhoneBuilder().build()
        AuthUI.IdpConfig.FacebookBuilder().build()
        AuthUI.IdpConfig.TwitterBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()*/
    )

    private lateinit var db: FirebaseFirestore

    /**
     * Cuando firebaseUI termine, volverá a este método que nos permitirá o abrir la
     * app principal o cerrarla
     */
    private fun resultadoAutenticacion(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Si estamos autenticados, guardamos el usuario en Firestore y vamos a la actividad Principal
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                val userId = it.uid
                val userEmail = it.email

                val userMap = hashMapOf(
                    "uid" to userId,
                    "email" to userEmail,
                )

                db.collection("usuarios").document(userId)
                    .set(userMap)
                    .addOnSuccessListener {
                        // Usuario guardado exitosamente, iniciamos la actividad principal
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar usuario: $e", Toast.LENGTH_LONG).show()
                    }
            }

        } else { //si hay error
            var msg_error = ""
            if (response == null) {
                // User pressed back button
                msg_error = "Es necesario autenticarse";
            } else if (response.getError()!!.getErrorCode() ==
                ErrorCodes.NO_NETWORK) {
                msg_error = "No hay red disponible para autenticarse";
            } else { //if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                msg_error = "Error desconocido al autenticarse";
            }
            Toast.makeText(
                this,
                msg_error,
                Toast.LENGTH_LONG)
                .show();
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        db = Firebase.firestore

        //Si el usuario está autenticado. Por defecto tenemos smartLock y puede estar
        //autenticado
        //vamos directamente a iniciar la app
        if (FirebaseAuth.getInstance().currentUser != null){
            finish()
            startActivity(Intent(this,MainActivity::class.java))
        }else{//iniciamos proceso autenticación
            // Create and launch sign-in intent
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                //con que permitimos autenticarnos(correo, telefono,google...)
                .setAvailableProviders(proveedoresAuth)
                //logo de la app. Solo se ve si tenemos varios proveedores
                // .setLogo(R.drawable.logo_ies)
                .build()
            autenticacionLauncher.launch(signInIntent)
        }

    }
}