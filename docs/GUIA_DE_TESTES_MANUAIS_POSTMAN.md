# Guia de Testes Manuais — Postman

Este guia cobre um passo a passo completo para validar manualmente, no Postman, todos os endpoints da API e as regras de negócio principais. Todos os cenários abaixo foram executados e conferidos contra a API real antes deste documento ser escrito — os status codes e corpos de resposta descritos são o comportamento **atual e correto**.

## 0. Preparação

### 0.1 Environment do Postman

Crie um Environment (`ecommerce-local`, por exemplo) com estas variáveis (deixe o `Initial value` e `Current value` vazios onde indicado — vão ser preenchidos pelos testes):

| Variável | Valor inicial |
|---|---|
| `baseUrl` | `http://localhost:8080/api` |
| `tokenAdmin` | (vazio) |
| `tokenUsuario` | (vazio) |
| `tokenVendedor` | (vazio) |
| `idCategoria` | (vazio) |
| `idProduto` | (vazio) |
| `idEndereco` | (vazio) |
| `idCarrinho` | (vazio) |
| `idPedido` | (vazio) |

Selecione esse Environment antes de rodar as requisições.

### 0.2 Usuários já disponíveis (seed automático)

A aplicação cria estes 3 usuários sozinha ao subir (ambiente de desenvolvimento):

| nomeUsuario | senha | perfis |
|---|---|---|
| `administrador` | `senhaAdministrador` | usuário + vendedor + administrador |
| `vendedor1` | `senha2` | vendedor |
| `usuario1` | `senha1` | usuário comum |

### 0.3 Autenticação nas requisições

Depois de logar, pegue o campo `token` da resposta e use em toda requisição protegida como header:
```
Authorization: Bearer {{tokenAdmin}}
```
(ou `{{tokenUsuario}}`, conforme o teste pedir). Em cada request de login abaixo, na aba **Tests** do Postman, adicione o script indicado para já salvar o token na variável automaticamente.

---

## 1. Autenticação (`/autenticacao`)

### 1.1 Cadastrar novo usuário (perfil padrão)
`POST {{baseUrl}}/autenticacao/cadastrar`
```json
{
  "nomeUsuario": "clienteTeste",
  "email": "clienteteste@exemplo.com",
  "senha": "senha123"
}
```
**Esperado:** `200 OK`, `{"mensagem":"Usuário registrado com sucesso!","status":true}`. Sem informar `perfis`, o usuário recebe automaticamente `PERFIL_USUARIO`.

### 1.2 Cadastrar usuário com perfil explícito
```json
{
  "nomeUsuario": "vendedorTeste",
  "email": "vendedorteste@exemplo.com",
  "senha": "senha123",
  "perfis": ["vendedor"]
}
```
**Esperado:** `200 OK`.

### 1.3 Cadastrar com nome de usuário duplicado
Repita o corpo do teste 1.1.
**Esperado:** `400`, `{"mensagem":"Erro: Nome de usuário já está em uso!","status":false}`.

### 1.4 Cadastrar com email duplicado
Mesmo `email` do teste 1.1, `nomeUsuario` diferente.
**Esperado:** `400`, `{"mensagem":"Erro: Email já está em uso!","status":false}`.

### 1.5 Cadastrar com perfil inválido
```json
{ "nomeUsuario": "outroUsuario", "email": "outro@exemplo.com", "senha": "senha123", "perfis": ["gerente"] }
```
**Esperado:** `400`, mensagem `"Perfil inválido: 'gerente'. Utilize 'usuario', 'vendedor' ou 'administrador'."`.

### 1.6 Cadastrar com corpo inválido (regra de validação)
```json
{ "nomeUsuario": "ab", "email": "nao-e-email", "senha": "123" }
```
**Esperado:** `400`, com `erros` mapeando `nomeUsuario` (mínimo 3), `email` (formato inválido) e `senha` (mínimo 6 caracteres).

### 1.7 Login válido — administrador
`POST {{baseUrl}}/autenticacao/entrar`
```json
{ "nomeUsuario": "administrador", "senha": "senhaAdministrador" }
```
**Esperado:** `200`, corpo com `id`, `nomeUsuario`, `perfis` (array com os 3 perfis) e `token`.
**Tests (Postman):**
```js
pm.environment.set("tokenAdmin", pm.response.json().token);
```

### 1.8 Login válido — usuario1 e vendedor1
Repita para `usuario1/senha1` (salve em `tokenUsuario`) e `vendedor1/senha2` (salve em `tokenVendedor`).

### 1.9 Login com senha errada
```json
{ "nomeUsuario": "usuario1", "senha": "senha-errada" }
```
**Esperado:** `404`, `{"mensagem":"Credenciais inválidas","status":false}`.

### 1.10 Login com usuário inexistente
```json
{ "nomeUsuario": "naoexiste", "senha": "qualquer" }
```
**Esperado:** `404` (mesmo comportamento do 1.9 — a API não revela se o problema foi usuário ou senha, o que é correto do ponto de vista de segurança).

### 1.11 Consultar nome do usuário logado
`GET {{baseUrl}}/autenticacao/nome-usuario` com header `Authorization: Bearer {{tokenUsuario}}`.
**Esperado:** `200`, corpo `"usuario1"` (string pura).

### 1.12 Consultar nome do usuário sem estar logado
Mesma URL, sem header `Authorization`.
**Esperado:** `200`, corpo `""` (string vazia — esse endpoint é público de propósito, para o frontend saber se há sessão ativa sem forçar erro).

### 1.13 Consultar detalhes do usuário logado
`GET {{baseUrl}}/autenticacao/usuario` com `Authorization: Bearer {{tokenUsuario}}`.
**Esperado:** `200`, com `id`, `nomeUsuario`, `perfis`, `token: null`.

### 1.14 Consultar detalhes sem token
Mesma URL sem header.
**Esperado:** `401`, `{"mensagem":"Não autorizado: é necessário estar autenticado para acessar este recurso.","status":false}`.

### 1.15 Logout
`POST {{baseUrl}}/autenticacao/sair` (sem token é necessário).
**Esperado:** `200`, `{"mensagem":"Você foi desconectado!","status":true}`, e o header `Set-Cookie` limpa o cookie JWT.

---

## 2. Categorias (`/categorias`)

> ⚠️ **Achado a documentar, não é bug de validação:** hoje, **qualquer usuário autenticado** (não só administrador) consegue criar/editar/excluir categorias — só os endpoints em `/administrador/**` exigem o perfil admin, e categorias não estão nesse namespace. Se a intenção do seu negócio é só admin gerenciar o catálogo, isso precisa de ajuste na regra de segurança; se for proposital (ex: vendedores também organizam categorias), está funcionando como projetado. Os testes abaixo documentam o comportamento **atual**.

### 2.1 Criar categoria (autenticado)
`POST {{baseUrl}}/categorias`, header `Authorization: Bearer {{tokenAdmin}}`
```json
{ "nomeCategoria": "Eletrônicos" }
```
**Esperado:** `201`, corpo com `idCategoria` e `nomeCategoria`.
**Tests:** `pm.environment.set("idCategoria", pm.response.json().idCategoria);`

### 2.2 Criar categoria sem token
**Esperado:** `401`.

### 2.3 Criar categoria com nome curto (regra de negócio)
```json
{ "nomeCategoria": "Ab" }
```
**Esperado:** `400`, erro em `nomeCategoria`: "deve ter entre 5 e 50 caracteres" (o nome precisa ter **no mínimo 5 caracteres** — `"Ab"` e até `"Abcd"` de 4 letras devem falhar).

### 2.4 Criar categoria com nome duplicado
Repita o corpo do 2.1.
**Esperado:** `400`, `"Categoria com o nome Eletrônicos ja existe!"`.

### 2.5 Buscar todas as categorias (paginação)
`GET {{baseUrl}}/categorias?numeroPagina=0&tamanhoPagina=5&ordenarPorCategoria=nomeCategoria&classificarOrdem=asc`
**Esperado:** `200`, com `conteudo`, `numeroPagina`, `tamanhoPagina`, `totalElementos`, `totalPaginas`, `paginaFinal`.

### 2.6 Buscar categoria por ID
`GET {{baseUrl}}/categorias/{{idCategoria}}`
**Esperado:** `200`.

### 2.7 Buscar categoria com ID inexistente
`GET {{baseUrl}}/categorias/999999`
**Esperado:** `404`, `"Categoria nao encontrado com id: 999999"`.

### 2.8 Buscar categoria com ID em formato inválido (regra de tipo)
`GET {{baseUrl}}/categorias/abc`
**Esperado:** `400`, mensagem explicando que `"abc"` é inválido para o parâmetro, esperado `Long`.

### 2.9 Atualizar categoria
`PUT {{baseUrl}}/categorias/{{idCategoria}}`
```json
{ "nomeCategoria": "Eletrônicos e Informática" }
```
**Esperado:** `200`.

### 2.10 Atualizar categoria para um nome já usado por outra
Crie uma segunda categoria (`"Roupas e Acessórios"`) e tente renomeá-la para `"Eletrônicos e Informática"`.
**Esperado:** `400`, `"Ja existe uma categoria com esse nome!"`.

### 2.11 Excluir categoria com produto vinculado (regra de integridade)
Depois de criar um produto na seção 3, tente `DELETE {{baseUrl}}/categorias/{{idCategoria}}`.
**Esperado:** `409 Conflict`, `"Não é possível excluir: existem registros vinculados a este recurso."`.

### 2.12 Excluir categoria sem vínculo
Crie uma categoria nova só para este teste e exclua-a.
**Esperado:** `200`, texto `"Categoria ... deletada com sucesso !!!"`.

### 2.13 Excluir categoria inexistente
`DELETE {{baseUrl}}/categorias/999999`
**Esperado:** `404`.

---

## 3. Produtos (`/produtos`, `/administrador/produtos`, `/public/produtos`)

### 3.1 Criar produto (admin)
`POST {{baseUrl}}/administrador/categorias/{{idCategoria}}/produto`, header `Authorization: Bearer {{tokenAdmin}}`
```json
{
  "nomeProduto": "Notebook GX",
  "descricao": "Notebook gamer com 16GB de RAM e SSD de 512GB",
  "quantidadeEstoque": 10,
  "preco": 3499.90,
  "desconto": 10
}
```
**Esperado:** `201`, e **`precoEspecial` deve vir exatamente `3149.91`** (3499.90 − 10%, arredondado a 2 casas). Esse é o teste mais importante de regra de negócio numérica: confirma que o cálculo de desconto está correto (sem erro de ponto flutuante).
**Tests:** `pm.environment.set("idProduto", pm.response.json().idProduto);`

### 3.2 Criar produto como usuário comum
Mesmo corpo, header com `{{tokenUsuario}}`.
**Esperado:** `403`.

### 3.3 Criar produto duplicado na mesma categoria
Repita o corpo do 3.1 na mesma categoria.
**Esperado:** `400`, `"Produto já existe!"`.

### 3.4 Criar produto com nome curto / descrição curta (validação)
```json
{ "nomeProduto": "AB", "descricao": "curta", "quantidadeEstoque": 1, "preco": 10, "desconto": 0 }
```
**Esperado:** `400`, erros em `nomeProduto` (mínimo 3) e `descricao` (mínimo 6).

### 3.5 Criar produto com descrição maior que 500 caracteres
Cole um texto com mais de 500 caracteres em `descricao`.
**Esperado:** `400` com erro de validação em `descricao` (não deve virar erro 500).

### 3.6 Criar produto com preço negativo/zero
```json
{ "nomeProduto": "Produto Invalido", "descricao": "Teste de preco invalido", "quantidadeEstoque": 1, "preco": 0, "desconto": 0 }
```
**Esperado:** `400`, `"O preço deve ser maior que zero"`.

### 3.7 Criar produto com desconto fora do intervalo
```json
{ "nomeProduto": "Produto Desconto Invalido", "descricao": "Teste de desconto invalido", "quantidadeEstoque": 1, "preco": 100, "desconto": 150 }
```
**Esperado:** `400`, `"O desconto não pode ser maior que 100%"`.

### 3.8 Criar produto em categoria inexistente
`POST {{baseUrl}}/administrador/categorias/999999/produto`
**Esperado:** `404`.

### 3.9 Buscar todos os produtos (público, sem token)
`GET {{baseUrl}}/public/produtos?numeroPagina=0&tamanhoPagina=10&ordenarPorProduto=preco&classificarOrdem=asc`
**Esperado:** `200`, mesmo sem header `Authorization`.

### 3.10 Buscar produtos por categoria
`GET {{baseUrl}}/public/categorias/{{idCategoria}}/produtos`
**Esperado:** `200`.

### 3.11 Buscar produtos por categoria sem produtos
Crie uma categoria nova (sem produtos) e chame o endpoint acima com o ID dela.
**Esperado:** `400`, `"A categoria ... não possui produtos."`.

### 3.12 Buscar produtos por palavra-chave
`GET {{baseUrl}}/public/produtos/palavra-chave/Notebook`
**Esperado:** `200`, encontra o produto criado em 3.1.

### 3.13 Buscar produtos por palavra-chave sem resultado
`GET {{baseUrl}}/public/produtos/palavra-chave/produtoquenaoexiste`
**Esperado:** `400`, `"Nenhum produto encontrado com a palavra-chave: produtoquenaoexiste"`.

### 3.14 Buscar produtos usando `%` como palavra-chave (regra de segurança/dados)
`GET {{baseUrl}}/public/produtos/palavra-chave/%25` (`%25` é o `%` codificado na URL)
**Esperado:** `400` (nenhum produto encontrado) — **não** deve retornar o catálogo inteiro. Se retornar tudo, o escape de wildcard do `LIKE` quebrou.

### 3.15 Ordenar por campo não permitido (whitelist)
`GET {{baseUrl}}/public/produtos?ordenarPorProduto=usuario`
**Esperado:** `400`, `"Campo de ordenação inválido: 'usuario'. Utilize um de: [...]"`. Tente também `ordenarPorProduto=idProduto);DROP TABLE produtos;--` só para confirmar que não quebra nada (deve dar o mesmo 400 de campo inválido).

### 3.16 Atualizar produto (admin) — recalcula preço especial
`PUT {{baseUrl}}/administrador/produtos/{{idProduto}}`
```json
{
  "nomeProduto": "Notebook GX",
  "descricao": "Notebook gamer com 16GB de RAM e SSD de 512GB",
  "quantidadeEstoque": 10,
  "preco": 4000.00,
  "desconto": 20
}
```
**Esperado:** `200`, `precoEspecial` recalculado para `3200.00`.

### 3.17 Atualizar produto como usuário comum
**Esperado:** `403`.

### 3.18 Atualizar produto inexistente
`PUT {{baseUrl}}/administrador/produtos/999999`
**Esperado:** `404`.

### 3.19 Atualizar imagem do produto (admin) — usar `form-data`
`PUT {{baseUrl}}/administrador/produtos/{{idProduto}}/imagem`, `Body → form-data`, chave `imagem` (tipo **File**), selecione um `.png` ou `.jpg` real.
**Esperado:** `200`, campo `imagem` do produto muda para um nome de arquivo `.png`/`.jpg` gerado (UUID).

### 3.20 Atualizar imagem como usuário comum
Mesmo request, header com `{{tokenUsuario}}`.
**Esperado:** `403` (esse endpoint é admin-only).

### 3.21 Tentar subir um arquivo que não é imagem
`form-data`, chave `imagem`, selecione um `.txt` ou `.pdf`.
**Esperado:** `400`, `"Tipo de arquivo não permitido. Envie uma imagem JPG, PNG, WEBP ou GIF."`.

### 3.22 Conferir se a imagem enviada é servida
Depois do teste 3.19, acesse no navegador (ou `GET` no Postman) `http://localhost:8080/imagens/<nome-do-arquivo-retornado>`.
**Esperado:** a imagem carrega, sem precisar de token (rota pública `/imagens/**`).

### 3.23 Excluir produto (admin)
Crie um produto novo só pra esse teste (sem vínculo com carrinho/pedido) e exclua.
**Esperado:** `200`.

### 3.24 Excluir produto que já está em algum pedido
Tente excluir o produto usado no checkout da seção 6.
**Esperado:** `409 Conflict` (produto referenciado em `itens_do_pedido`).

---

## 4. Carrinho (`/carrinhos`)

> Use o token de **`usuario1`** (`{{tokenUsuario}}`) daqui pra frente, simulando um cliente comprando.

### 4.1 Adicionar produto ao carrinho
`POST {{baseUrl}}/carrinhos/produtos/{{idProduto}}/quantidade/2`, header `Authorization: Bearer {{tokenUsuario}}`
**Esperado:** `201`. Confira: `precoTotal` do carrinho = `precoEspecial do produto × 2`.
**Tests:** `pm.environment.set("idCarrinho", pm.response.json().idCarrinho);`

### 4.2 Conferir que o estoque foi reservado
`GET {{baseUrl}}/public/produtos/palavra-chave/Notebook`
**Esperado:** `quantidadeEstoque` do produto caiu em 2 (regra de negócio: o estoque é reservado no momento em que o item entra no carrinho, não só na hora da compra).

### 4.3 Adicionar o mesmo produto de novo (duplicado)
Repita 4.1.
**Esperado:** `400`, `"Produto ... já existe no carrinho."` (o fluxo certo para aumentar quantidade é o endpoint de atualização, não adicionar de novo).

### 4.4 Adicionar quantidade maior que o estoque disponível
`POST {{baseUrl}}/carrinhos/produtos/{{idProduto}}/quantidade/999`
**Esperado:** `400`, `"Por favor, faça um pedido do ... menor ou igual à quantidade ..."`.

### 4.5 Adicionar quantidade zero ou negativa
`POST {{baseUrl}}/carrinhos/produtos/{{idProduto}}/quantidade/0`
**Esperado:** `400`, `"A quantidade deve ser maior que zero."`.

### 4.6 Adicionar produto inexistente ao carrinho
`POST {{baseUrl}}/carrinhos/produtos/999999/quantidade/1`
**Esperado:** `404`.

### 4.7 Consultar o carrinho do usuário logado
`GET {{baseUrl}}/carrinhos/usuarios/carrinho`
**Esperado:** `200`, com a lista de itens e o `precoTotal` batendo com a soma dos itens.

### 4.8 Incrementar quantidade de um item
`PUT {{baseUrl}}/carrinhos/produtos/{{idProduto}}/quantidade/adicionar`
**Esperado:** `200`, quantidade do item +1, `precoTotal` recalculado, e o estoque do produto cai mais 1 unidade.

### 4.9 Decrementar quantidade de um item
`PUT {{baseUrl}}/carrinhos/produtos/{{idProduto}}/quantidade/deletar`
**Esperado:** `200`, quantidade −1, `precoTotal` recalculado, e o estoque **devolve** 1 unidade.

### 4.10 Decrementar até chegar a zero
Repita o decremento até a quantidade do item chegar a 1 e decremente mais uma vez.
**Esperado:** `200`, o item some da lista do carrinho (é removido automaticamente quando a quantidade chega a zero), e o estoque devolve por completo.

### 4.11 Operação inválida na URL
`PUT {{baseUrl}}/carrinhos/produtos/{{idProduto}}/quantidade/aumentar` (nome errado, não é `adicionar` nem `deletar`)
**Esperado:** `400`, `"Operação inválida. Utilize 'adicionar' ou 'deletar'."`.

### 4.12 Remover produto específico do carrinho
Adicione o produto de novo (`POST .../quantidade/1`) e depois:
`DELETE {{baseUrl}}/carrinhos/{{idCarrinho}}/produto/{{idProduto}}`
**Esperado:** `200`, texto de sucesso, e o estoque é totalmente devolvido.

### 4.13 Remover produto de um carrinho que não é seu (regra de segurança)
Faça login com `vendedor1` e tente `DELETE {{baseUrl}}/carrinhos/{{idCarrinho}}/produto/{{idProduto}}` usando `{{tokenVendedor}}`, mas com o `idCarrinho` do `usuario1`.
**Esperado:** `404` (a API não deixa transparecer que o carrinho existe — trata como "não encontrado" em vez de "não autorizado", o que é a forma correta de não vazar informação de outro usuário).

### 4.14 Listar todos os carrinhos (admin)
`GET {{baseUrl}}/administrador/carrinhos`, header `{{tokenAdmin}}`
**Esperado:** `200` (lista todos os carrinhos do sistema — inclusive de outros usuários, por isso é admin-only).

### 4.15 Listar todos os carrinhos como usuário comum
**Esperado:** `403`.

---

## 5. Endereços (`/enderecos`)

### 5.1 Criar endereço
`POST {{baseUrl}}/enderecos`, header `{{tokenUsuario}}`
```json
{ "rua": "Rua das Flores", "numeroRua": "123", "cidade": "Uberlândia", "estado": "MG", "pais": "Brasil", "cep": "38400-000" }
```
**Esperado:** `201`.
**Tests:** `pm.environment.set("idEndereco", pm.response.json().idEndereco);`

### 5.2 Criar endereço com campos curtos (regra de negócio alinhada)
```json
{ "rua": "Rua", "numeroRua": "1", "cidade": "SP", "estado": "M", "pais": "Br", "cep": "38400-000" }
```
**Esperado:** `400`, com erros em `rua` (mínimo 5), `cidade` (mínimo 3) e `estado` (mínimo 2). *(Esses três já causaram erro 500 antes de uma correção recente — confira que agora vem 400 com mensagem clara.)*

### 5.3 Criar endereço com CEP em formato inválido
```json
{ "rua": "Rua das Flores", "numeroRua": "123", "cidade": "Uberlândia", "estado": "MG", "pais": "Brasil", "cep": "abc123" }
```
**Esperado:** `400`, `"CEP inválido. Formato esperado: 00000-000"`.

### 5.4 Buscar endereços do usuário logado
`GET {{baseUrl}}/enderecos/usuarios`, header `{{tokenUsuario}}`
**Esperado:** `200`, só os endereços desse usuário.

### 5.5 Buscar endereço por ID
`GET {{baseUrl}}/enderecos/{{idEndereco}}`
**Esperado:** `200`.

### 5.6 Buscar endereço de outro usuário pelo ID (regra de segurança)
Faça login com `vendedor1` e tente `GET {{baseUrl}}/enderecos/{{idEndereco}}` (que pertence ao `usuario1`) usando `{{tokenVendedor}}`.
**Esperado:** `404` (mesma lógica do 4.13: não revela que o endereço existe).

### 5.7 Atualizar endereço
`PUT {{baseUrl}}/enderecos/{{idEndereco}}`, header `{{tokenUsuario}}`
```json
{ "rua": "Rua das Palmeiras", "numeroRua": "456", "cidade": "Uberlândia", "estado": "MG", "pais": "Brasil", "cep": "38400-100" }
```
**Esperado:** `200`.

### 5.8 Atualizar endereço de outro usuário
Mesmo request com `{{tokenVendedor}}`.
**Esperado:** `404`.

### 5.9 Listar todos os endereços (admin)
`GET {{baseUrl}}/administrador/enderecos`, header `{{tokenAdmin}}`
**Esperado:** `200`, todos os endereços do sistema.

### 5.10 Excluir endereço
Crie um segundo endereço só para excluir, e rode `DELETE {{baseUrl}}/enderecos/{idEndereco}`.
**Esperado:** `200`.

---

## 6. Pedidos (`/pedido`, `/pedidos`)

> **Antes de começar:** garanta que `usuario1` tem pelo menos 1 item no carrinho (repita 4.1 se precisar) e um endereço válido (`{{idEndereco}}`).

### 6.1 Finalizar pedido (checkout)
`POST {{baseUrl}}/pedido/usuarios/pagamentos/cartao_credito`, header `{{tokenUsuario}}`
```json
{
  "idEndereco": {{idEndereco}},
  "nomeGateway": "Stripe",
  "idPagamentoGateway": "pay_teste123",
  "statusGateway": "aprovado",
  "mensagemRespostaGateway": "Pagamento aprovado com sucesso"
}
```
**Esperado:** `201`. Confira 3 coisas importantes:
1. Não retorna erro 500 (esse checkout já teve um bug grave de concorrência — se voltar 500, é regressão séria).
2. `statusPedido` vem **`"PENDENTE"`**, mesmo você tendo mandado `statusGateway: "aprovado"` — a API não confia no que o cliente diz sobre o pagamento.
3. `valorTotal` bate com o `precoTotal` que o carrinho tinha antes do checkout.

**Tests:** `pm.environment.set("idPedido", pm.response.json().idPedido);`

### 6.2 Conferir que o carrinho esvaziou
`GET {{baseUrl}}/carrinhos/usuarios/carrinho`
**Esperado:** `200`, `itens: []`, `precoTotal: 0`.

### 6.3 Conferir que o estoque NÃO foi descontado de novo
Anote a quantidade em estoque do produto antes do checkout (via 4.2) e confira de novo agora.
**Esperado:** quantidade igual à de antes do checkout (o estoque só é descontado uma vez, no momento em que o item entra no carrinho — não de novo na finalização).

### 6.4 Checkout com carrinho vazio
Repita 6.1 imediatamente de novo (carrinho já está vazio).
**Esperado:** `400`, `"O carrinho está vazio"`.

### 6.5 Checkout com endereço de outro usuário
Faça login com `vendedor1`, adicione algo ao carrinho dele, e tente finalizar o pedido usando o `idEndereco` do `usuario1`.
**Esperado:** `404` (endereço não pertence a quem está comprando).

### 6.6 Checkout sem `idEndereco` no corpo
```json
{ "nomeGateway": "Stripe" }
```
**Esperado:** `400`, erro de validação em `idEndereco` ("é obrigatório").

### 6.7 Consultar meus pedidos
`GET {{baseUrl}}/pedidos/usuarios`, header `{{tokenUsuario}}`
**Esperado:** `200`, lista com o pedido do 6.1, mais recente primeiro.

### 6.8 Consultar pedidos de um usuário sem nenhum pedido
Faça login com um usuário recém-cadastrado (ex: `clienteTeste` do 1.1) e chame o mesmo endpoint.
**Esperado:** `200`, lista vazia `[]` (não deve dar erro).

### 6.9 Atualizar status do pedido — pular etapa (regra de transição)
`PUT {{baseUrl}}/administrador/pedidos/{{idPedido}}/status`, header `{{tokenAdmin}}`
```json
{ "novoStatus": "ENTREGUE" }
```
**Esperado:** `400`, `"Não é possível mudar o status de PENDENTE para ENTREGUE."` (o pedido está `PENDENTE`, e o único próximo passo válido é `PAGO` ou `CANCELADO`).

### 6.10 Confirmar pagamento
```json
{ "novoStatus": "PAGO" }
```
**Esperado:** `200`, `statusPedido: "PAGO"`.

### 6.11 Avançar para enviado
```json
{ "novoStatus": "ENVIADO" }
```
**Esperado:** `200`.

### 6.12 Avançar para entregue
```json
{ "novoStatus": "ENTREGUE" }
```
**Esperado:** `200`.

### 6.13 Tentar "reabrir" um pedido entregue
```json
{ "novoStatus": "PAGO" }
```
**Esperado:** `400` (não existe transição de `ENTREGUE` para nenhum outro status — é estado final).

### 6.14 Atualizar status como usuário comum
Repita 6.10 com `{{tokenUsuario}}`.
**Esperado:** `403`.

### 6.15 Atualizar status de pedido inexistente
`PUT {{baseUrl}}/administrador/pedidos/999999/status`
**Esperado:** `404`.

### 6.16 Atualizar status sem informar `novoStatus`
```json
{}
```
**Esperado:** `400`, erro de validação (`novoStatus` é obrigatório).

---

## 7. Segurança e formato de erro (testes transversais)

Esses testes não são de um endpoint específico — validam que o **contrato de erro é consistente** em toda a API. Vale rodar em qualquer request protegida.

### 7.1 Requisição sem token em rota protegida
**Esperado:** `401`, sempre no formato `{"mensagem": "...", "status": false}`.

### 7.2 Requisição com token de usuário sem o perfil necessário
**Esperado:** `403`, sempre `{"mensagem": "...", "status": false}`.

### 7.3 Requisição com token expirado ou adulterado
Pegue um token válido, mude alguns caracteres do meio (mantendo o formato JWT) e chame qualquer rota protegida.
**Esperado:** `401` (o filtro de JWT rejeita o token e a requisição segue como não-autenticada).

### 7.4 Rotas antigas que eram públicas por engano
`GET http://localhost:8080/h2-console/` e `GET {{baseUrl}}/test/qualquercoisa`
**Esperado:** `401` em ambas (não são mais públicas; a segunda nem existe como endpoint real, então cai em "não autenticado" antes de chegar a "não encontrado").

### 7.5 Corpo de requisição corrompido (JSON inválido)
Em qualquer `POST`/`PUT`, mande um corpo tipo `{ "nomeCategoria": }` (JSON quebrado de propósito).
**Esperado:** `400`, `"Corpo da requisição inválido: verifique o formato dos campos enviados."`.

### 7.6 Path variable com tipo errado
`GET {{baseUrl}}/categorias/nao-e-um-numero`
**Esperado:** `400`, mensagem explicando o tipo esperado (`Long`).

### 7.7 CORS — origem não autorizada
No Postman isso não é fácil de simular fielmente (o Postman não aplica CORS como um navegador), então esse teste é melhor feito direto no navegador: abra o console DevTools em qualquer página que não seja `http://localhost:5173`, rode:
```js
fetch("http://localhost:8080/api/public/produtos").then(r => r.status)
```
**Esperado:** bloqueado pelo navegador com erro de CORS (a API só libera a origem configurada em `aplicacao.cors.origens-permitidas`).

---

## 8. Resumo rápido — tabela de regras de negócio críticas

Use esta tabela como checklist final antes de considerar a API pronta:

| Regra | Como confirmar | Seção |
|---|---|---|
| Estoque só é descontado uma vez por compra (ao entrar no carrinho, não de novo no checkout) | 4.2 + 6.3 | 4, 6 |
| Checkout não trava com `ConcurrentModificationException` | 6.1 | 6 |
| Cliente não consegue forjar "pagamento aprovado" | 6.1 (statusPedido vem PENDENTE) | 6 |
| Transição de status de pedido segue uma ordem lógica | 6.9 a 6.13 | 6 |
| Cálculo de preço com desconto é exato (sem erro de ponto flutuante) | 3.1, 3.16 | 3 |
| Usuário não acessa/edita endereço ou carrinho de outra pessoa | 4.13, 5.6, 5.8 | 4, 5 |
| Upload de imagem só aceita imagem de verdade, e só admin faz | 3.19 a 3.21 | 3 |
| Endpoints administrativos exigem perfil admin | 3.2, 3.17, 4.15, 6.14 | 3, 4, 6 |
| Erros sempre no mesmo formato `{mensagem, status, erros?}` | 7.1 a 7.6 | 7 |
| Exclusão é bloqueada quando há vínculo (categoria com produto, produto com pedido) | 2.11, 3.24 | 2, 3 |

---

## 9. Pontos em aberto para você decidir (não são bugs, são decisões de produto)

- **Categorias sem restrição de perfil:** qualquer usuário autenticado cria/edita/exclui categorias hoje (seção 2). Se isso deveria ser admin-only como produtos são, me avise que eu ajusto a regra de segurança.
- **Vendedor (`PERFIL_VENDEDOR`) não tem hoje nenhuma permissão diferente de um usuário comum** — os únicos endpoints restritos são os de `PERFIL_ADMINISTRADOR`. Se a ideia é vendedor gerenciar só os próprios produtos, isso ainda não existe e precisa ser desenhado.
