package com.example.blogapp.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.blogapp.R
import com.example.blogapp.home.HomeActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Signinandregister_activity : AppCompatActivity() {

    private var currentMode = "login"

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore


    lateinit var btnLogin: Button
    lateinit var btnRegister: Button
    lateinit var etEmail: TextInputEditText
    lateinit var etPassword: TextInputEditText
    lateinit var cardView: CardView
    lateinit var etRegName: TextInputEditText
    lateinit var etRegEmail: TextInputEditText
    lateinit var etRegPassword: TextInputEditText
    lateinit var tvNewHere: TextView
    lateinit var ivProfile: ImageView
    private var selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                ivProfile.setImageURI(uri)
            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signinandregister)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Correctly initialize Cloudinary MediaManager
        if (MediaManager.get() == null) {
            val config = HashMap<String, String>()
            config["cloud_name"] = "dengfxb5y" // cloud name
            config["api_key"] = "963746953362443" // api key
            config["api_secret"] = "cPW6sUo31yHx1DYE5phMjflts9s" // api secret
            MediaManager.init(this, config)
        }


        initViews()
        setListeners()

        // Intent mode
        val mode = intent.getStringExtra("auth_mode")
        if (mode == "register") {
            showRegisterUI()
        } else {
            showLoginUI()
        }

        // Back press handling
        onBackPressedDispatcher.addCallback(this) {
            if (currentMode == "register") {
                showLoginUI()
            } else {
                finishAffinity()
            }
        }
    }

    private fun initViews() {
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        cardView = findViewById(R.id.cardView)
        etRegName = findViewById(R.id.etRegName)
        etRegEmail = findViewById(R.id.etRegisterEmail)
        etRegPassword = findViewById(R.id.etRegisterPassword)
        tvNewHere = findViewById(R.id.tvNewHere)
        ivProfile = findViewById(R.id.ivProfile)
    }

    private fun setListeners() {
        btnLogin.setOnClickListener {

            if (currentMode == "login") {
                loginUser()


            } else {
                showLoginUI()
            }
        }
        btnRegister.setOnClickListener {
            if (currentMode == "register") {
                registerUser()
            } else {

                showRegisterUI()
            }
        }
        cardView.setOnClickListener {
            pickImage.launch("image/*")

        }
    }

    //login
    private fun loginUser() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }


        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()

                } else {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showLoginUI() {
        currentMode = "login"

        btnLogin.setBackgroundColor(getColor(R.color.red))
        btnLogin.setTextColor(getColor(android.R.color.white))

        btnRegister.setBackgroundColor(getColor(android.R.color.transparent))
        btnRegister.setTextColor(getColor(R.color.black))

        etEmail.visibility = View.VISIBLE
        etPassword.visibility = View.VISIBLE
        tvNewHere.visibility = View.VISIBLE

        cardView.visibility = View.GONE
        etRegName.visibility = View.GONE
        etRegEmail.visibility = View.GONE
        etRegPassword.visibility = View.GONE
    }

    // register
    private fun registerUser() {
        val name = etRegName.text.toString()
        val email = etRegEmail.text.toString()
        val password = etRegPassword.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {

            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Start the image upload after successful authentication
                    uploadImageToCloudinary(name, email)
                } else {
                    Toast.makeText(
                        this,
                        "Register Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun uploadImageToCloudinary(name: String, email: String) {
        selectedImageUri?.let { uri ->
            MediaManager.get().upload(uri)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        Toast.makeText(
                            this@Signinandregister_activity,
                            "Image upload started...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val imageUrl = resultData?.get("url")?.toString()
                        if (imageUrl != null) {
                            // If image upload is successful, save user data to Firestore
                            saveUserToFirestore(name, email, imageUrl)
                        } else {
                            Toast.makeText(
                                this@Signinandregister_activity,
                                "Image URL not found.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Toast.makeText(
                            this@Signinandregister_activity,
                            "Image upload failed: ${error?.description}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
        }
    }

    private fun saveUserToFirestore(name: String, email: String, imageUrl: String) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val user = hashMapOf(
                "name" to name,
                "email" to email,
                "profileImageUrl" to imageUrl,
                "userId" to userId
            )

            firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to save user data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }


    private fun showRegisterUI() {
        currentMode = "register"

        btnRegister.setBackgroundColor(getColor(R.color.blue))
        btnRegister.setTextColor(getColor(android.R.color.white))

        btnLogin.setBackgroundColor(getColor(android.R.color.transparent))
        btnLogin.setTextColor(getColor(R.color.black))

        etEmail.visibility = View.GONE
        etPassword.visibility = View.GONE
        tvNewHere.visibility = View.GONE

        cardView.visibility = View.VISIBLE
        etRegName.visibility = View.VISIBLE
        etRegEmail.visibility = View.VISIBLE
        etRegPassword.visibility = View.VISIBLE
    }
}
