package com.example.galest

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.galest.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        carregarNomeUtilizador()

        binding.botaoJogarOffline.setOnClickListener {
            criarJogoOffline()
        }

        binding.botaoCriarJogoOnline.setOnClickListener {
            criarJogoOnline()
        }

        binding.botaoEntrarJogoOnline.setOnClickListener {
            entrarJogoOnline()
        }

        binding.menuButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }
    }

    private fun carregarNomeUtilizador() {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid
            firestore.collection("users").document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        binding.idSaudacaoJogador?.text = "Olá, Utilizador!"
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val nome = snapshot.getString("name") ?: "Utilizador"
                        binding.idSaudacaoJogador?.text = "Olá, $nome!"
                    } else {
                        val nome = user.displayName ?: "Utilizador"
                        binding.idSaudacaoJogador?.text = "Olá, $nome!"
                    }
                }
        } else {
            binding.idSaudacaoJogador?.text = "Olá!"
        }
    }


    private fun entrarJogoOnline() {
        val idJogo = binding.idJogoInput?.text.toString()
        if (idJogo.isEmpty()) {
            binding.idJogoInput?.error = "Por favor introduza um ID de jogo"
            return
        }
        GameData.meuId = "O"
        Firebase.firestore.collection("jogos")
            .document(idJogo)
            .get()
            .addOnSuccessListener {
                val model = it.toObject(GameModel::class.java)
                if (model == null) {
                    binding.idJogoInput?.error = "Por favor introduza um ID de jogo válido"
                } else {
                    model.estadoJogo = EstadoJogo.ENTROU_EM_JOGO
                    GameData.salvarModeloJogo(model)
                    comecarJogo()
                }
            }
    }

    private fun criarJogoOnline() {
        GameData.meuId = "X"
        GameData.salvarModeloJogo(
            GameModel(
                estadoJogo = EstadoJogo.CRIADO,
                idJogo = Random.nextInt(1000, 9999).toString()
            )
        )
        comecarJogo()
    }

    fun criarJogoOffline() {
        GameData.salvarModeloJogo(
            GameModel(
                estadoJogo = EstadoJogo.ENTROU_EM_JOGO
            )
        )
        comecarJogo()
    }

    fun comecarJogo() {
        startActivity(Intent(this, GameActivity::class.java))
    }
}
