# Storage Engine
Este trabalho consiste na implementação de uma storage engine, implementação dos componentes Gerenciador de Arquivos e Gerenciador de Buffer de um SGBD.

## Arquitetura
![estrut-storage-engine-v2](https://user-images.githubusercontent.com/41158713/52479224-aa0b7380-2b86-11e9-8804-34b849294bd1.jpg)

## Arquitetura detalhada
### 1) Representação
![tabela](https://user-images.githubusercontent.com/41158713/53306264-9a1faf00-3869-11e9-8400-42b1f061507e.jpg)

### 2) Bloco de controle
Os dados de um bloco de controle são salvos em bytes da seguinte forma: COLAI6|COLBA25|
![bloco controle](https://user-images.githubusercontent.com/41158713/53306261-99871880-3869-11e9-8cd2-fca365bc9935.jpg)

### 3) Bloco de dados
As tuplas são inseridas em um array de dados que representa o conteúdo do Tuple Directory e as Tuplas.

- OBS: Tupla Directory é um conjunto de elementos onde cada elemento representa o index do começo de uma Tupla

#### 3.1) Bloco de dados com apenas uma tupla
Adicionando a tupla:  505|REBECA ALBUQUERQUE|
![bloco dados ex 1](https://user-images.githubusercontent.com/41158713/53306262-99871880-3869-11e9-97d5-68ffacea7af9.jpg)

#### 3.2) Bloco de dados com mais de uma tupla
Adicionando as tuplas:  5|RE| e 6|BR|
![bloco dados ex 2](https://user-images.githubusercontent.com/41158713/53306263-99871880-3869-11e9-8099-4f9ecc8c21bb.jpg)

## Gerenciador de Arquivos
Esta classe é responsável por armazenar os bytes dos Blocos (Controle e Dados) em um arquivo, .txt por exemplo. É sugerido usar a classe Random Access File para essa ação.

A ação de criar um arquivo com os bytes dos Blocos significa que se está criando uma tabela, onde o arquivo é a tabela, os dados do Bloco de Controle são as informações referente as colunas da 
tabela e os dados dos Blocos de Dados são as tuplas.

Além de criar tabelas, o Gerenciador de Arquivos é responsável por ler uma tabela e criar um arquivo contendo os RowIDs (ids das tuplas) de uma tabela. Para isso ele carrega o Bloco de Controle 
de um arquivo e depois seus Blocos de dados.

- OBS1: Lembrando que 1 Bloco de Dados possui n tuplas
- OBS2: Na hora de escrever na tabela/arquivo tem que escrever também os bytes referentes a informações como: id do bloco, tipo do bloco e etc, ou seja, tem que escrever todos os bytes do Bloco 
de Dados e do Bloco de Controle
- OBS3: Só é para criar um arquivo com os RowIDs de todas as tabelas
- OBS4: Exemplo de RowID: 1-0-219 é o equivalente a idFile = 1, idBloco = 0 e idTupla = 219
- OBS5: Container = File = Arquivo = Tabela

## Gerenciador de Buffer
O Gerenciador de Buffer é responsável por manipular Pages Requests, onde Page Request é apenas o id do arquivo e o id de um bloco (em memória, um bloco passa a ser chamado de Pagina). Essa 
manipulação faz uso da política LRU e funciona da seguinte forma: Ao receber um PageID o Gerenciador de Buffer vai verificar se existe uma Pagina com aquele id em memória, se não existir ele 
usa o Gerenciador de Arquivo para buscar um Bloco com aquele PageID. Uma vez que a memória está cheia e o Gerenciador de Buffer recebe um novo Page Request, ele utiliza a política do LRU para
saber qual bloco tirar da memória. 

Informações para acompanhar o funcionamento do Gerenciador de Buffer: quantidade de Miss (quando o PageID solicitado não está em memória), quantidade de Hit
 (quando o PageID está em memória) e taxa de Hit (hit/(hit + miss))