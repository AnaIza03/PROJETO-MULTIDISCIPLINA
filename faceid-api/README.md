Aqui está o conteúdo do seu **README.md** com uma estrutura profissional, utilizando ícones, tabelas e os espaços reservados para as suas capturas de tela e configurações.

Basta copiar o código abaixo e colar no seu arquivo:

-----

# 🛡️ FaceID API: Autenticação Biométrica Enterprise

API de autenticação biométrica de alto nível desenvolvida para o projeto multidisciplinar. O sistema utiliza **Inteligência Artificial nativa** (sem APIs externas pagas) para realizar o reconhecimento facial através de vetores matemáticos (embeddings).

-----

## 📸 Demonstração do Sistema

> **[IMAGEM 1: ]**

-----

## 🛠️ Tecnologias Utilizadas

### **Backend & Segurança**

  * **Java 17** & **Spring Boot 3.3.5**
  * **Spring Security 6** (Proteção de rotas)
  * **JWT (JSON Web Token)** para persistência de sessão
  * **BCrypt** para criptografia de senhas no banco

### **Inteligência Artificial**

  * **DJL (Deep Java Library)**: Engine para rodar modelos de IA em Java.
  * **FaceNet (PyTorch)**: Modelo pré-treinado para extração de 512 características faciais.
  * **Cosine Similarity**: Algoritmo matemático para comparação de faces.

### **Banco de Dados**

  * **MySQL 9.0**: Armazenamento persistente de credenciais e vetores biométricos.

-----

## ⚙️ Configuração do Ambiente

### **1. Banco de Dados & Conexão**

Abaixo está a configuração utilizada no arquivo `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/faceid_db?createDatabaseIfNotExist=true
    username: root
    password: ${DB_PASSWORD:SUA_SENHA_PARATESTAR} 
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

### **2. Estrutura da Tabela**

O sistema utiliza uma tabela otimizada para armazenar os dados binários da face (BLOB).

> **[IMAGEM 2: ]**
 
-----

## 🚀 Como Executar o Projeto

### **Passo 1: Gerar o Modelo de IA**

Certifique-se de ter o Python instalado e execute o script de exportação:

```bash
pip install facenet-pytorch torch
python export_model.py
```

### **Passo 2: Compilar e Rodar a API**

No terminal do seu projeto:

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

O sistema estará disponível em: `http://localhost:8080`

-----

## 🧠 Como a Biometria Funciona?

1.  **Captura**: A imagem da webcam é enviada para a API em formato Base64/Multipart.
2.  **Extração**: O modelo FaceNet processa a imagem e gera um vetor matemático único de 512 dimensões.
3.  **Comparação**: No login, o sistema compara o vetor "ao vivo" com o vetor guardado no banco usando a **Similaridade de Cosseno**:

$$\text{similarity} = \frac{\mathbf{A} \cdot \mathbf{B}}{\|\mathbf{A}\| \|\mathbf{B}\|}$$

  * **Resultado \> 0.85**: Acesso Permitido ✅
  * **Resultado \< 0.85**: Acesso Negado ❌

-----

## 📌 API Endpoints

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Cadastra novo usuário com foto. |
| `POST` | `/api/auth/login` | Autenticação facial e geração de JWT. |
| `GET` | `/api/users/profile` | Retorna dados do usuário autenticado. |


## 🛠️ Roadmap de Desenvolvimento

[x] Extração de Embeddings com FaceNet.

[x] Persistência de vetores em MySQL.

[x] Autenticação via JWT.

[ ] Implementação de Liveness Detection (prova de vida).

[ ] Suporte a múltiplos rostos por usuário.
-----

**Desenvolvido por Ana Izabelle.**
*Projeto Acadêmico Multidisciplinar - 2026*