package com.example.galest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

object GameData {
    private var _gameModel : MutableLiveData<GameModel> = MutableLiveData()
    var gameModel : LiveData<GameModel> = _gameModel
    var meuId : String = ""


    fun salvarModeloJogo(model : GameModel){
        _gameModel.postValue(model)
        if(model.idJogo != "-1") {
            Firebase.firestore.collection("jogos")
                .document(model.idJogo)
                .set(model)
        }
    }

    fun carregarModeloJogo(){
        gameModel.value.apply {
            if(this!!.idJogo != "-1"){
                Firebase.firestore.collection("jogos")
                    .document(this.idJogo)
                    .addSnapshotListener { value, error ->
                        val model = value?.toObject(GameModel::class.java)
                        if(model != null)
                            salvarModeloJogo(model)
                    }
            }
        }
    }
}