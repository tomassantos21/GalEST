package com.example.galest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.galest.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityGameBinding
    private var gameModel : GameModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack?.setOnClickListener {
            finish() // Fecha esta activity e volta à anterior
        }

        GameData.carregarModeloJogo()

        binding.botao0.setOnClickListener(this)
        binding.botao1.setOnClickListener(this)
        binding.botao2.setOnClickListener(this)
        binding.botao3.setOnClickListener(this)
        binding.botao4.setOnClickListener(this)
        binding.botao5.setOnClickListener(this)
        binding.botao6.setOnClickListener(this)
        binding.botao7.setOnClickListener(this)
        binding.botao8.setOnClickListener(this)

        binding.botaoComecarJogo.setOnClickListener {
            comecarJogo()
        }

        GameData.gameModel.observe(this){
            gameModel = it
            setUI()
        }
    }

    fun setUI(){
        gameModel?.apply {
            binding.botao0.text = filledPos[0]
            binding.botao1.text = filledPos[1]
            binding.botao2.text = filledPos[2]
            binding.botao3.text = filledPos[3]
            binding.botao4.text = filledPos[4]
            binding.botao5.text = filledPos[5]
            binding.botao6.text = filledPos[6]
            binding.botao7.text = filledPos[7]
            binding.botao8.text = filledPos[8]

            binding.botaoComecarJogo.visibility = View.VISIBLE

            binding.textoEstadoJogo.text =
                when(estadoJogo){
                    EstadoJogo.CRIADO -> {
                        "ID Jogo: $idJogo"
                    }
                    EstadoJogo.ENTROU_EM_JOGO -> {
                        "Clica em Começar Jogo para iniciar o jogo"
                    }
                    EstadoJogo.EM_ANDAMENTO -> {
                        binding.botaoComecarJogo.visibility = View.INVISIBLE
                        if (GameData.meuId.isNullOrEmpty()) {
                            // Jogo offline
                            "É a vez da peça $jogadorAtual"
                        } else {
                            // Jogo online
                            when (GameData.meuId) {
                                jogadorAtual -> "É a sua vez"
                                else -> "É a vez do oponente"
                            }
                        }
                    }
                    EstadoJogo.FINALIZADO -> {
                        if(vencedor.isNotEmpty()) {
                            when(GameData.meuId){
                                vencedor -> "Você ganhou o jogo"
                                else -> "$vencedor ganhou o jogo"
                            }
                            "$vencedor ganhou o jogo"
                        } else {
                            "Empate"
                        }
                    }
                }
        }
    }


    fun comecarJogo(){
        gameModel?.apply {
            atualizarDadosJogo(
                GameModel(
                    idJogo = idJogo,
                    estadoJogo = EstadoJogo.EM_ANDAMENTO
                )
            )
        }
    }

    fun atualizarDadosJogo(model : GameModel){
        GameData.salvarModeloJogo(model)
    }

    fun verificarVencedor(){
        val posicoesVencedoras = arrayOf(
            intArrayOf(0,1,2),
            intArrayOf(3,4,5),
            intArrayOf(6,7,8),
            intArrayOf(0,3,6),
            intArrayOf(1,4,7),
            intArrayOf(2,5,8),
            intArrayOf(0,4,8),
            intArrayOf(2,4,6)
        )

        gameModel?.apply {
            for (i in posicoesVencedoras) {
                if (filledPos[i[0]] == filledPos[i[1]] &&
                    filledPos[i[1]] == filledPos[i[2]] &&
                    filledPos[i[0]].isNotEmpty()
                ) {
                    estadoJogo = EstadoJogo.FINALIZADO
                    vencedor = filledPos[i[0]]
                }
            }

            if (filledPos.none() { it.isEmpty() }){
                estadoJogo = EstadoJogo.FINALIZADO
            }
            atualizarDadosJogo(this)
        }
    }

    override fun onClick(v: View?) {
        gameModel?.apply {
            if(estadoJogo != EstadoJogo.EM_ANDAMENTO){
                Toast.makeText(applicationContext, "Jogo não iniciado", Toast.LENGTH_SHORT).show()
                return
            }

            if(idJogo != "-1" && jogadorAtual != GameData.meuId){
                Toast.makeText(applicationContext, "Não é a sua vez", Toast.LENGTH_SHORT).show()
                return
            }
            val posicaoClicada = (v?.tag as String).toInt()

            if(filledPos[posicaoClicada].isEmpty()){
                filledPos[posicaoClicada] = jogadorAtual
                jogadorAtual = if(jogadorAtual == "X") "O" else "X"
                verificarVencedor()
                atualizarDadosJogo(this)
            }
        }
    }
}