package com.example.galest

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText

    private lateinit var saveButton: Button
    private lateinit var editButton: ImageButton
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referências UI
        nameInput = findViewById(R.id.editTextName)
        emailInput = findViewById(R.id.editTextEmail)
        phoneInput = findViewById(R.id.editTextPhone)

        saveButton = findViewById(R.id.buttonSave)
        editButton = findViewById(R.id.buttonEdit)
        backButton = findViewById(R.id.buttonBack)

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Nenhum utilizador autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Botão voltar
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val uid = user.uid
        emailInput.setText(user.email ?: "")

        // Carregar dados do Firestore
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    nameInput.setText(doc.getString("name") ?: "")
                    phoneInput.setText(doc.getString("phone") ?: "")
                } else {
                    nameInput.setText(user.displayName ?: "")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }

        // Ativar edição ao clicar no botão de editar
        editButton.setOnClickListener {
            nameInput.isEnabled = true
            phoneInput.isEnabled = true
            saveButton.visibility = Button.VISIBLE
        }

        // Guardar alterações
        saveButton.setOnClickListener {
            val newName = nameInput.text.toString().trim()
            val newPhone = phoneInput.text.toString().trim()

            if (newName.isEmpty()) {
                nameInput.error = "O nome não pode estar vazio"
                return@setOnClickListener
            }

            val data = mapOf(
                "name" to newName,
                "phone" to newPhone,
                "email" to user.email
            )

            firestore.collection("users").document(uid).set(data)
                .addOnSuccessListener {
                    val updates = userProfileChangeRequest {
                        displayName = newName
                    }

                    user.updateProfile(updates)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show()
                            nameInput.isEnabled = false
                            phoneInput.isEnabled = false
                            saveButton.visibility = Button.GONE
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao atualizar perfil", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao guardar no Firestore", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
