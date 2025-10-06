# 💰 FinTrack — Gerenciador Financeiro Pessoal

O FinTrack é um aplicativo desenvolvido em Java (Spring Boot) que ajuda usuários a organizar suas finanças pessoais, registrando transações, acompanhando gastos e armazenando comprovantes de pagamento diretamente no Google Drive.

###   Funcionalidades Principais

* Cadastro de transações (receitas e despesas)

* Upload de comprovantes (PDF, PNG, JPEG) com envio automático ao Google Drive

* Autenticação via Google OAuth 2.0

* Banco de dados MySQL para persistência de dados

* Integração com API do Google Drive

* Arquitetura RESTful (backend em Java + Spring Boot)

### Tecnologias Utilizadas
**Back-end:**
* Java 17+

* Spring Boot

* Spring Security (OAuth2)

* JPA / Hibernate

* MySQL

**Integrações:**

* Google Drive API

* OAuth2 (Google Authentication)

**Outros:**

* Maven

* Lombok

* WebClient

* Multipart Upload

### Configuração do Projeto

#### Clonar o repositório
```

git clone https://github.com/silvaeverton/fin_Track.git
cd fin_Track
```
#### Configurar variáveis de ambiente
```

GOOGLE_CLIENT_ID=seu-client-id
GOOGLE_CLIENT_SECRET=seu-client-secret
DB_URL=jdbc:mysql://localhost:3306/fintrack?useSSL=false&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=sua_senha
```

#### Executar o projeto

```
mvn spring-boot:run
```
A aplicação iniciará em:
👉 http://localhost:8080

#### Autenticação com Google

**1** Vá até o Google Cloud Console

**2** Crie um OAuth 2.0 Client ID.

**3** Configure os URIs de redirecionamento autorizados, por exemplo:
```Java

http://localhost:8080/login/oauth2/code/google

```
Adicione o client-id e client-secret como variáveis de ambiente no .env.

#### Upload de Comprovantes

Ao criar uma transação com anexo (PDF, PNG, JPEG), o arquivo é enviado automaticamente para uma pasta específica no Google Drive, e o link público é armazenado junto da transação.

