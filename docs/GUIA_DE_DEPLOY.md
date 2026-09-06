# Guia de Deploy — Point da Chama

Objetivo: colocar o backend (`ecommerce`) e o frontend (`ecommerce-loja`) no ar com uma URL pública, para compartilhar com qualquer pessoa (ex: sua mãe).

Caminho escolhido: **Neon** (banco Postgres gratuito) + **Render** (backend) + **Vercel** (frontend). Todos têm plano gratuito suficiente para um projeto de portfólio.

## O que já está pronto no código

- `application.properties`: `spring.datasource.url`, `spring.datasource.username`, `imagem.base.url` e `server.port` agora são configuráveis por variável de ambiente (`DB_URL`, `DB_USERNAME`, `IMAGEM_BASE_URL`, `PORT`), com os valores de hoje como padrão — rodar localmente continua funcionando exatamente igual, sem precisar setar nada.
- CORS (`aplicacao.cors.origens-permitidas`) já é configurável via `FRONTEND_URLS` (feito em sessão anterior).
- Frontend (`ecommerce-loja`): a URL da API já é lida de `VITE_API_URL` (variável de ambiente do Vite) — nenhuma mudança de código necessária.

## Passo 1 — Banco de dados (Neon)

1. Crie uma conta em https://neon.tech (dá pra entrar com GitHub ou Google).
2. Crie um novo projeto/banco Postgres (nome sugerido: `ecommerce`).
3. Copie a "Connection string" que o Neon fornece. Vai ser algo como:
   `postgresql://usuario:senha@ep-xxxxx.us-east-2.aws.neon.tech/ecommerce?sslmode=require`
4. Guarde essas 3 partes separadas: usuário, senha, e o restante da URL (host + banco) — vai precisar delas no passo 2.

## Passo 2 — Backend (Render)

1. Crie conta em https://render.com e conecte sua conta do GitHub.
2. "New +" → "Web Service" → selecione o repositório `Joaoneto1011/ecommerce` (já está no GitHub).
3. Configurações do serviço:
   - **Runtime**: Java (ou "Docker" se o Render pedir; a opção Maven nativa também funciona)
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/ecommerce-0.0.1-SNAPSHOT.jar` (confira o nome exato do .jar gerado em `target/` após o build local, com `mvn clean package`)
4. Em "Environment Variables", adicione:
   | Variável | Valor |
   |---|---|
   | `DB_URL` | `jdbc:postgresql://ep-xxxxx.neon.tech/ecommerce?sslmode=require` (monte a partir da connection string do Neon, trocando `postgresql://` por `jdbc:postgresql://` e tirando usuário/senha da URL) |
   | `DB_USERNAME` | usuário do Neon |
   | `DB_PASSWORD` | senha do Neon |
   | `JWT_SECRET` | uma string longa e aleatória (ex: gere com `openssl rand -base64 48`) |
   | `FRONTEND_URLS` | deixe em branco por enquanto, volta aqui no Passo 4 |
   | `IMAGEM_BASE_URL` | deixe em branco por enquanto, volta aqui no Passo 4 |
5. Deploy. O Render vai te dar uma URL fixa, tipo `https://ecommerce-xxxx.onrender.com`. Anote essa URL.

**Atenção — plano gratuito do Render:**
- O serviço "dorme" depois de ~15 min sem acesso. O primeiro acesso depois disso demora de 30 a 60 segundos pra "acordar" — é normal, não é erro.
- O disco é **efêmero**: qualquer imagem de produto enviada pelo painel admin (upload) é perdida quando o serviço reinicia ou é reimplantado. As fotos do cardápio inicial (semeadas via script) sobrevivem até o próximo redeploy, mas novos uploads não são permanentes nesse plano. Se isso importar no futuro, a solução é migrar o armazenamento de imagens para um serviço externo (Cloudinary, S3, Supabase Storage) — posso fazer isso depois, é uma tarefa separada.

## Passo 3 — Frontend (Vercel)

O projeto `ecommerce-loja` ainda não está no GitHub (só existe na sua máquina). Antes deste passo, decida comigo se quer que eu crie um repositório novo e suba o código, ou se prefere subir você mesmo.

1. Crie conta em https://vercel.com, conecte com GitHub.
2. "Add New..." → "Project" → selecione o repositório `ecommerce-loja`.
3. Em "Environment Variables", adicione:
   | Variável | Valor |
   |---|---|
   | `VITE_API_URL` | `https://ecommerce-xxxx.onrender.com/api` (a URL do Render do Passo 2 + `/api`) |
4. Deploy. A Vercel gera uma URL tipo `https://point-da-chama.vercel.app` (ou você pode escolher um subdomínio customizado nas configurações do projeto, gratuito).

## Passo 4 — Fechar o ciclo

1. Volte nas variáveis de ambiente do Render (Passo 2) e preencha:
   - `FRONTEND_URLS` = a URL da Vercel (ex: `https://point-da-chama.vercel.app`)
   - `IMAGEM_BASE_URL` = a própria URL do Render + `/imagens` (ex: `https://ecommerce-xxxx.onrender.com/imagens`)
2. Redeploy do backend no Render (necessário pra aplicar as novas variáveis).
3. Rode o script de seed do cardápio (`seed_cardapio.mjs`) apontando para a URL de produção em vez de `localhost:8080` — ajusto o script quando chegar nessa etapa.
4. Acesse a URL da Vercel, teste login, cardápio, checkout e o painel admin.
5. Envie o link da Vercel para quem quiser — esse é o link final.

## Resumo do que cada serviço guarda

- **Neon**: os dados (produtos, categorias, pedidos, usuários).
- **Render**: a API (backend Spring Boot) + as imagens enviadas (com a ressalva do disco efêmero acima).
- **Vercel**: a interface (frontend React) que todo mundo acessa pelo navegador.
