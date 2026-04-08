package com.aulasandroid.consumoapi

import android.R.attr.label
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aulasandroid.consumoapi.model.Endereco
import com.aulasandroid.consumoapi.service.RetrofitFactory
import com.aulasandroid.consumoapi.ui.theme.ConsumoAPITheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConsumoAPITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CepScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun CepScreen(modifier: Modifier = Modifier) {
    var cepState by remember { mutableStateOf("") }
    var ufState by remember { mutableStateOf("") }
    var cidadeState by remember { mutableStateOf("") }
    var ruaState by remember { mutableStateOf("") }

    var listaEnderecos by remember {
        mutableStateOf(listOf<Endereco>())
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Blue)
                .padding(top = 16.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CONSULTA CEP",
                fontSize = 24.sp,
                color = Color.White
            )
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .offset(y = (-30).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Encontre o seu endereço",
                    fontSize = 20.sp
                )

                // Pesquisa por cep
                OutlinedTextField(
                    value = cepState,
                    onValueChange = {
                        cepState = it

                        if (cepState.length == 8) {
                            val call = RetrofitFactory().getEnderecoService().getEnderecoByCep(cep = cepState)

                            call.enqueue(object : Callback<Endereco> {
                                override fun onResponse(call: Call<Endereco>, response: Response<Endereco>) {
                                    if (response.isSuccessful) {
                                        response.body()?.let { endereco ->
                                            listaEnderecos = listOf(endereco)
                                        }
                                        ufState = listaEnderecos[0].uf
                                        cidadeState = listaEnderecos[0].cidade
                                        ruaState = listaEnderecos[0].rua
                                    } else {
                                        Log.e("TESTE", "Erro na API: ${response.code()}")
                                    }
                                }

                                override fun onFailure(call: Call<Endereco>, t: Throwable) {
                                    Log.e("TESTE", "Falha na rede: ${t.message}")
                                }
                            })
                        }},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Qual CEP está buscando?") },
                    trailingIcon = {
                        IconButton( onClick = {
                            val call = RetrofitFactory().getEnderecoService().getEnderecoByCep( cep = cepState )

                            call.enqueue(object: Callback<Endereco> {
                                override fun onResponse(
                                    call: Call<Endereco>,
                                    response: Response<Endereco>
                                ) {
//                                Log.i("TESTE", "${response.body()}")
                                    listaEnderecos = listOf(response.body()!!)
                                }

                                override fun onFailure(
                                    call: Call<Endereco>,
                                    t: Throwable
                                ) {
                                    Log.i("TESTE", "${t.message}")
                                }
                            })
                        } ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = ""
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Row() {
                    OutlinedTextField(
                        value = ufState,
                        onValueChange = { ufState = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        label = { Text( text = "UF?" ) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Characters
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = cidadeState,
                        onValueChange = { cidadeState = it },
                        modifier = Modifier.weight(2f),
                        label = { Text( text = "Qual a cidade?" ) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words
                        )
                    )
                }

                Row( verticalAlignment = Alignment.CenterVertically ) {
                    OutlinedTextField(
                        value = ruaState,
                        onValueChange = { ruaState = it },
                        modifier = Modifier.weight(2f),
                        label = { Text(text = "Qual o nome da rua?") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words
                        )
                    )

                    // Pesquisa por UF, Cidade e Rua
                    IconButton( onClick = {

                            val call = RetrofitFactory().getEnderecoService().getEnderecosByUfCidadeRua(
                                uf = ufState,
                                cidade = cidadeState,
                                rua = ruaState
                            )

                            call.enqueue(object: Callback<List<Endereco>> {
                                override fun onResponse(
                                    call: Call<List<Endereco>>,
                                    response: Response<List<Endereco>>
                                ) {
//                                Log.i("TESTE", "${response.body()}")
                                    listaEnderecos = response.body()!!
                                }

                                override fun onFailure(
                                    call: Call<List<Endereco>>,
                                    t: Throwable
                                ) {
                                    Log.i("TESTE", "${t.message}")
                                }
                            })


                    } ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = ""
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(listaEnderecos) {
                CardEndereco(it)
            }
        }
    }
}

@Composable
fun CardEndereco( endereco: Endereco) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "CEP: ${endereco.cep}")
            Text(text = "Rua: ${endereco.rua}")
            Text(text = "Cidade: ${endereco.cidade}")
            Text(text = "Bairro: ${endereco.bairro}")
            Text(text = "UF: ${endereco.uf}")
        }
    }
}