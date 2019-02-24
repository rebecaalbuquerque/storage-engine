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

#### &nbsp;&nbsp;&nbsp; 3.1) Bloco de dados com apenas uma tupla
&nbsp;&nbsp;&nbsp;&nbsp; Adicionando a tupla:  505|REBECA ALBUQUERQUE|
![bloco dados ex 1](https://user-images.githubusercontent.com/41158713/53306262-99871880-3869-11e9-97d5-68ffacea7af9.jpg)

#### &nbsp;&nbsp;&nbsp; 3.2) Bloco de dados com mais de uma tupla
&nbsp;&nbsp;&nbsp;&nbsp; Adicionando as tuplas:  5|RE| e 6|BR|
![bloco dados ex 2](https://user-images.githubusercontent.com/41158713/53306263-99871880-3869-11e9-8099-4f9ecc8c21bb.jpg)
