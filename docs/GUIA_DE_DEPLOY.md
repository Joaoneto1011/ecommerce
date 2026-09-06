# Guia de Deploy — Point da Chama

Registro de como o backend (`ecommerce`) e o frontend (`ecommerce-frontend`) foram colocados no ar. Caminho usado: **Neon** (banco Postgres) + **Render** (backend, via Docker) + **Vercel** (frontend) + **Cloudinary** (imagens de produto). Todos com plano gratuito.

## URLs em produção

- Backend: `https://ecommerce-tjbn.onrender.com`
- Frontend: `https://ecommerce-frontend-beta-opal.vercel.app`
- Repositórios: `github.com/Joaoneto1011/ecommerce` (backend) e `github.com/Joaoneto1011/ecommerce-frontend` (frontend)

## O que está pronto no código

- `application.properties`: `spring.datasource.url`, `spring.datasource.username`, `imagem.base.url`, `server.port` e as credenciais do Cloudinary são configuráveis por variável de ambiente. Rodar localmente continua igual, com os mesmos padrões de antes (exceto Cloudinary, ver abaixo).
- CORS (`aplicacao.cors.origens-permitidas`) configurável via `FRONTEND_URLS`.
- `Dockerfile` na raiz do backend — o Render builda a imagem Java a partir dele (não existe runtime nativo "Java" no Render, só Docker).
- Frontend: `VITE_API_URL` já era configurável desde a criação do projeto. `vercel.json` com rewrite `/(.*) → /index.html` — obrigatório para apps React (SPA) na Vercel, senão qualquer acesso direto a uma rota (ex: `/produto/1`, `/admin`, F5 na página) retorna 404 do servidor antes do React Router assumir.
- Upload de imagem de produto usa **Cloudinary** (`ImplementacaoArquivoService`), não mais disco local.

## Variáveis de ambiente do backend (Render)

| Variável | Valor |
|---|---|
| `DB_URL` | connection string do Neon, formato `jdbc:postgresql://<host>/<banco>?sslmode=require` |
| `DB_USERNAME` | usuário do Neon |
| `DB_PASSWORD` | senha do Neon |
| `JWT_SECRET` | string aleatória longa |
| `FRONTEND_URLS` | URL(s) do frontend, separadas por vírgula |
| `CLOUDINARY_CLOUD_NAME` | do dashboard do Cloudinary |
| `CLOUDINARY_API_KEY` | do dashboard do Cloudinary |
| `CLOUDINARY_API_SECRET` | do dashboard do Cloudinary |

`IMAGEM_BASE_URL` não é mais necessária — o Cloudinary já retorna URLs absolutas.

## Por que Cloudinary (e não disco local do Render)

O plano gratuito do Render tem disco **efêmero**: qualquer arquivo salvo localmente (como as fotos enviadas pelo painel admin) é apagado sempre que o serviço "dorme" por inatividade (~15 min) e volta a responder — não só em redeploys, como se pensava inicialmente. Isso já causou fotos quebradas em produção. A migração para o Cloudinary (armazenamento externo, plano gratuito generoso) resolve isso de forma definitiva: as imagens ficam hospedadas fora do Render e sobrevivem a qualquer reinício do serviço.

Se um dia as imagens sumirem de novo (não deveria mais acontecer), o script `reparar_imagens_prod.mjs` (na pasta scratchpad da sessão que fez esse trabalho) reenvia as 20 fotos do cardápio via API admin.

## Plano gratuito do Render — cold start

O serviço "dorme" após ~15 min sem acesso. O primeiro acesso depois disso demora de 30 a 60 segundos pra "acordar" — normal, não é erro. Se isso incomodar, o plano pago (a partir de ~$7/mês) remove esse comportamento.

## Passo a passo resumido (para replicar em outro ambiente)

1. **Neon**: criar projeto Postgres, copiar connection string.
2. **Render**: New Web Service → conectar repo do backend → Language "Docker" → região próxima ao banco → plano Free → configurar as variáveis de ambiente da tabela acima → Deploy.
3. **Cloudinary**: criar conta gratuita, pegar Cloud Name / API Key / API Secret no dashboard.
4. **Vercel**: New Project → conectar repo do frontend → variável `VITE_API_URL` = URL do Render + `/api` → Deploy.
5. Voltar no Render e completar `FRONTEND_URLS` com a URL da Vercel → redeploy.
6. Rodar o script de seed do cardápio contra a URL de produção.
7. Testar tudo (catálogo, carrinho, checkout, painel admin) na URL da Vercel.

## Resumo do que cada serviço guarda

- **Neon**: dados (produtos, categorias, pedidos, usuários).
- **Cloudinary**: fotos dos produtos.
- **Render**: a API (backend Spring Boot).
- **Vercel**: a interface (frontend React).
