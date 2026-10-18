# REQUISITOS FUNCIONAIS

## RF01 — CADASTRAR FUNCIONÁRIO
O sistema deve permitir o cadastro de funcionários. O usuário deve informar obrigatoriamente CPF e nome, podendo opcionalmente informar uma equipe. O CPF é um campo único.

## RF02 — ALTERAR FUNCIONÁRIO
O sistema deve permitir alterar CPF, nome e equipe do funcionário.

## RF03 — BUSCAR FUNCIONÁRIO
O sistema deve permitir buscar os funcionários de acordo com os critérios: nome parcial ou CPF. Caso nenhum critério seja informado, a busca deve retornar todos os funcionários. A busca deve retornar id, CPF, nome do funcionário, nome de usuário, tipo de usuário e equipe.

## RF04 — CADASTRAR USUÁRIO
O sistema deve permitir o cadastro de usuários para os funcionários. Após selecionar o funcionário, deve ser informado o nome de usuário, senha e tipo de usuário. O nome de usuário é um campo único e cada funcionário deve ter no máximo um usuário.

## RF05 — ALTERAR USUÁRIO
O sistema deve permitir alterar nome de usuário, senha e tipo do usuário.

## RF06 — BUSCAR USUÁRIO
O sistema deve permitir buscar usuários por id, nome de usuário ou listar todos os usuários. A busca deve retornar id, nome de usuário e tipo de usuário.

## RF07 — APAGAR USUÁRIO
O sistema deve permitir a exclusão de usuários.

## RF08 — CADASTRAR EQUIPE
O sistema deve permitir o cadastro de equipe. O usuário deve informar obrigatoriamente o nome da equipe.

## RF09 — ALTERAR EQUIPE
O sistema deve permitir alterar o nome da equipe.

## RF10 — BUSCAR EQUIPE
O sistema deve permitir buscar equipes usando como critério o nome da equipe. Caso não seja informado o critério, a busca deve retornar todas as equipes. A busca deve retornar id e nome.

## RF11 — APAGAR EQUIPE
O sistema deve permitir a exclusão de equipes.


## RF12 — CADASTRAR BANCO
O sistema deve permitir o cadastro de bancos. O usuário deve informar obrigatoriamente o código do banco (COMPE) e o nome do banco. O código do banco é um valor único.

## RF13 — ALTERAR BANCO
O sistema deve permitir alterar o código e nome do banco.

## RF14 — BUSCAR BANCO
O sistema deve permitir buscar os bancos usando como critério: código ou nome parcial. Caso nenhum critério seja informado, a busca deve retornar todos os bancos. A busca deve retornar id, código do banco e nome do banco.

## RF15 — CADASTRAR PROPOSTA
O sistema deve permitir o cadastro de propostas. O usuário deve informar obrigatoriamente código da proposta, valor, data de geração, funcionário responsável, banco em que foi gerado e cliente a quem se destina a proposta, podendo informar opcionalmente, a data de pagamento.

## RF16 — ALTERAR PROPOSTA
O sistema deve permitir alterar código, valor, data de geração e data de pagamento das propostas.

## RF17 — BUSCAR PROPOSTA
O sistema deve permitir buscar as propostas usando como critério opcionais: código da proposta, nome do funcionário responsável e código do banco. A busca deve ter como critério obrigatório um intervalo de duas datas que deve ser referente à data de geração ou data de pagamento. Caso o código da proposta seja informado, as datas devem ser ignoradas na busca. A busca deve retornar código da proposta, valor, data de geração, data de pagamento, status, nome do funcionário, nome do banco, CPF do cliente e nome do cliente.

## RF18 — CADASTRAR CLIENTE
O sistema deve permitir o cadastro de clientes. O usuário deve informar obrigatoriamente CPF, nome, telefone e data de nascimento do cliente. O CPF é um campo único.

## RF19 — ALTERAR CLIENTE
O sistema deve permitir alterar CPF, nome, telefone e data de nascimento dos clientes.

## RF20 — BUSCAR CLIENTE
O sistema deve permitir buscar clientes com os critérios: CPF, nome parcial ou telefone parcial. Caso nenhum critério seja informado, a busca deve retornar todos os clientes. A busca deve retornar id, CPF, nome, telefone e data de nascimento.


## RF21 — EXPORTAR PARA CSV
Para cada busca realizada conforme os requisitos ## RF03, ## RF06, ## RF10, ## RF14, ## RF17 e ## RF20, o usuário pode optar por exportar os resultados para um arquivo CSV que será baixado pelo computador do usuário.

## RF22 — GERAR RELATÓRIO DE RESULTADOS DO DIA
O sistema deve apresentar uma listagem de valores negociados por consultor. Deve apresentar nome do funcionário, quantidade de propostas cadastradas no dia, soma dos valores de propostas com status GERADA no dia, soma dos valores de propostas com status CONTRATADA no dia, soma dos valores de propostas com status CONTRATADA no mês e tendência de resultado mensal. A tendência deve ser calculada como: soma de propostas contratadas no mês dividido pela quantidade de dias decorridos do mês e multiplicado pela quantidade total de dias do mês.
Propostas com status CANCELADA não devem ser consideradas no relatório.

## RF23 — ORGANIZAR RELATÓRIO
A tela de relatório do ## RF22 deve gerar listagens separadas por equipe, com subtotais calculados para cada equipe e um total calculado para todas as equipes listadas. O usuário pode selecionar quantas equipes quiser para gerar o relatório.

## RF24 — RESTRIÇÃO DE DATA DO RELATÓRIO
O relatório deve ser gerado com a data do dia atual, não sendo possível informar uma data.

## RF25 — RESTRIÇÃO DE USUÁRIO ADMINISTRATIVO
O tipo de usuário GESTOR deve ter acesso a todas as funcionalidades do sistema.

## RF26 — RESTRIÇÃO DE USUÁRIO CONSULTOR
O tipo de usuário CONSULTOR deve ter acesso a cadastro, alteração e busca de apenas suas próprias propostas; cadastro, alteração e busca de clientes; busca de bancos; exportação para CSV.

## RF27 — VALIDAÇÃO DE CPF
Os campos de CPF do funcionário e do cliente devem ser validados usando cálculo do dígito validador do CPF, evitando assim a inserção de CPF inválido.

## RF28 — CONTROLE DE STATUS DA PROPOSTA
As propostas podem ter status CONTRATADA, GERADA ou CANCELADA. O sistema inicializa as propostas com status GERADA. Quando o usuário informar data de pagamento, o sistema altera o status para CONTRATADA. O usuário pode cancelar qualquer proposta, na qual o sistema altera o status para CANCELADA e remove a data de pagamento.

## RF29 — ACESSO DOS USUÁRIOS
Para acessar o sistema os usuários precisam fazer login usando usuário e senha.
