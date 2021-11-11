import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/*
Esse c�digo � respons�vel por enviar as caracter�sticas de �udio de uma m�sica (valence, energy, etc)
e obter de volta a classe prevista (triste, alegre, etc). O servi�o que receber� esses dados � o que foi
feito deploy nos v�deos - o servi�o web do nosso modelo na Azure.

Funciona em 4 passos principais:

1) Constr�i o objeto da chamada HTTP a ser enviado ao servi�o web do modelo
2) Coloca como o `body` da chamada HTTP as caracter�sticas de �udio que s�o classificados
3) Envia a requisi��o ao servi�o
4) Recebe a resposta em JSON e a converte para uma List de HashMaps. O retorno do modelo
   cont�m as caracter�sticas de cada �udio enviadas, a probabilidade de cada set de caracter�sticas
   pertencer a uma classe e a classe prevista.
*/

public class Main {
    // Endpoint do modelo. Para mais informa��es, ver o seguinte
    // v�deo no tempo j� marcado: https://youtu.be/jTUvOlWBuVw?t=188.
    // O endpoint est� presente no campo "REST Endpoint" no servi�o web do modelo.
    private static final String MODEL_URL = "http://2363c596-fe22-4ed1-8b74-f2a75043a9c6.brazilsouth.azurecontainer.io/score";
    
    // Chave de API do seu servi�o na Azure. Para mais informa��es assistir o seguinte
    // v�deo no tempo j� marcado: https://youtu.be/jTUvOlWBuVw?t=188
    // A chave est� presente no campo "Primary Key" no servi�o web do modelo.
    private static final String API_KEY = "2pHqdyrstWwbeTFnxh5kwM3BwOVgyyqn";

    public static void main(String[] Args) throws Exception {
        // Constru�mos a nosso objeto HTTP que ser� enviado ao servidor do modelo. 
        // O `API_KEY` � utilizado nos headers e os dados enviados s�o atribu�dos ao objeto
        // na linha 43 por meio da fun��o `.sampleData`
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(MODEL_URL))
                .headers("Content-Type", "application/json", "Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(sampleData()))
                .build();

        try {
            // Realiza-se a chamada HTTP para o servidor do modelo. O objeto `client` definido na linha 40
            // chama o m�todo `#send` passando o request da linha 41, que � quem cont�m as informa��es da URL, 
            // autentica��o com a API_KEY e os dados a serem classificados.
            HttpResponse<String> response  = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Convertemos a reposta para uma List de objetos de HashMap. Nas linhas 86-111 h� um exemplo de retorno
            // da fun��o `.responseMapBody`.
            List<Map<String, Object>> classification = responseMapBody(response.body());
            System.out.println(classification);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Cria caracteristicas fakes de �udio a serem classificadas, neste caso h� somente um objeto
    // na lista, por�m podem ser quantos forem necess�rios. Mais explicadas no v�deo https://youtu.be/jTUvOlWBuVw
    private static String sampleData() {
        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("class", "");
        item.put("valence", 3.9394);
        item.put("energy", 1);
        item.put("liveness", 1);
        item.put("speechiness", 1);
        item.put("instrumentalness", 1);
        item.put("tempo", 150);
        item.put("danceability", 8.393);
        item.put("acousticness", 0.992);

        array.add(item);

        return array.toString();
    }

    /*
    Recebe como argumento o retorno da chamada ao modelo, que � uma string, a
    converte para JSON e depois para uma lista de HashMap.
        
    Exemplo de retorno:
    [
      {
         "Scored Probabilities_feliz": 2.0532116841698607E-6,
         "Scored Probabilities_dormir": 9.48652830187802E-28,
         "Scored Probabilities_foco": 2.0256458418315827E-14,
         "Scored Probabilities_correr": 0.9982468202418197,
         "Scored Probabilities_gaming": 0.001751024180401978,
         "Scored Probabilities_energetico": 1.9553258735535537E-11,
         "Scored Probabilities_triste": 6.480293395209065E-16,
         "Scored Probabilities_calmo": 1.0234652002373781E-7,
         "liveness": 1.0,
         "tempo": 150.0,
         "valence": 3.9394,
         "instrumentalness": 1.0,
         "danceability: 8.393,
         "speechiness": 1.0,
         "acousticness": 0.992,
         "class": "", 
         "energy" 1.0
         "Scored Labels": "correr", 
      },
      {
        ...      
      },
      ...
    ]
    
    Cada HashMap cont�m as caracter�sticas de �udio que foram enviadas para a classifica��o (liveness,
    tempo, valence, instrumentalness, danceability, speechiness, acousticness, energy), a probabilidade
    delas pertencerem a cada uma das classes (Scored Probabilities_feliz, Scored Probabilities_dormir,
    Scored Probabilities_correr, Scored Probabilities_gaming, Scored Probabilities_energetico,
    Scored Probabilities_triste, Scored Probabilities_calmo) e, por fim, a classe prevista (Scored Labels),
    que nada mais � a classe com a maior probabilidade das caracter�sticas pertencerem
    */
    private static List<Map<String, Object>> responseMapBody(String body) {
        Map<String, Object> hm;
        List<Map<String, Object>> res = new ArrayList<>();

        // Parseia a resposta em string para JSON
        Object obj = JSONValue.parse(body);
        JSONObject jsonObject = (JSONObject) obj;
        
        // O retorno do modelo vem dentro da chave `result` 
        JSONArray objs = (JSONArray) jsonObject.get("result"); 

        // Iterar sobre os objetos do `result`, onde cada um representa o resultado da classifica��o de um set
        // de caracter�sticas de �udio do Spotify
        for (Object _obj : objs) {
            hm = new HashMap<>();
            // Obt�m o set de chaves do objeto e itera sobre eles, acessando o valor de cada um 
            // e o adicionando no dicion�rio em Java a ser retornado
            for (Object o : ((JSONObject) _obj).keySet()) {
                String key = (String) o;
                hm.put(key, ((JSONObject) _obj).get(key));
            }
            res.add(hm);
        }

        return res;
    }
}