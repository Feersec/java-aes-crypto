# 🔑 CriptoJ - Ferramenta de Criptografia AES

Uma ferramenta de linha de comando, desenvolvida em Java, para criptografar e descriptografar arquivos usando o algoritmo AES-256. Este projeto de portfólio demonstra a aplicação prática de conceitos de criptografia simétrica e o uso seguro da Java Cryptography Extension (JCE).

## ✨ Funcionalidades

-   **Criptografia Forte:** Utiliza o padrão AES com chaves de 256 bits.
-   **Derivação Segura de Chave:** Gera a chave de criptografia a partir de uma senha fornecida pelo usuário usando o algoritmo PBKDF2WithHmacSHA256, o que previne ataques de dicionário e força bruta.
-   **Segurança de Nível de Bloco:** Emprega o modo de operação CBC (Cipher Block Chaining) com um Vetor de Inicialização (IV) e um "Sal" (Salt) aleatórios para cada criptografia, garantindo que arquivos idênticos criptografados com a mesma senha resultem em saídas diferentes.

## 🛠️ Tecnologias Utilizadas

-   **[Java](https://www.java.com/ )**: Linguagem de programação principal.
-   **Java Cryptography Extension (`javax.crypto`)**: Biblioteca nativa do Java para operações criptográficas avançadas.
    -   `Cipher`: Para as operações de criptografia e descriptografia.
    -   `SecretKeyFactory` e `PBEKeySpec`: Para a derivação segura da chave a partir da senha.

## 🚀 Como Compilar e Executar

Para executar este projeto, você precisa ter o Java Development Kit (JDK) instalado.

### Compilação

1.  Clone o repositório e navegue até a pasta do projeto.
    ```bash
    git clone https://github.com/Feersec/java-cripto.git
    cd java-cripto
    ```
2.  Compile o código-fonte:
    ```bash
    javac CriptoJ.java
    ```

### Execução

A ferramenta opera em dois modos: `-e` (encrypt ) e `-d` (decrypt).

**Para Criptografar:**
```bash
java CriptoJ -e <sua_senha> <arquivo_original> <arquivo_de_saida.enc>
