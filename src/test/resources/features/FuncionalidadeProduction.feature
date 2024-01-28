# language: pt
Funcionalidade: API - Production

  Cenário: Alterar Status do pedido para pronto
    Dado Dado que tenho um pedido na fila
    Quando e quero passar ele para pronto
    Entao devo conseguir alterar o status

  Cenário: Alterar Status do pedido para Finalizado
    Dado Dado que tenho um pedido no status pronto
    Quando e quero passar ele para finalizado
    Entao devo conseguir alterar para finalizado

  Cenário: Pesquisar Pedido
    Dado Dado que tenho um pedido cadastrado
    E e tenho o id do pedido
    Entao devo conseguir buscar o pedido que esta sendo produzido

