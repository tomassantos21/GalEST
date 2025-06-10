package com.example.galest

import kotlin.random.Random

data class GameModel (
    var idJogo : String = "-1",
    var filledPos : MutableList<String> = mutableListOf("","","","","","","","",""),
    var vencedor : String = "",
    var estadoJogo : EstadoJogo = EstadoJogo.CRIADO,
    var jogadorAtual : String = (arrayOf("X", "O"))[Random.nextInt(2)]
)

enum class EstadoJogo {
    CRIADO,
    ENTROU_EM_JOGO,
    EM_ANDAMENTO,
    FINALIZADO
}