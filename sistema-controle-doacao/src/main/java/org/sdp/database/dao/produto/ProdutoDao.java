package org.sdp.database.dao.produto;

import org.sdp.database.dao.IGenericDAO;
import org.sdp.database.dao.doacao.DoacaoProdutoDao;
import org.sdp.model.DoacaoProduto;
import org.sdp.model.Produto;
import org.sdp.util.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProdutoDao implements IGenericDAO<Produto, Long> {

    private final EntityManager em;


    public ProdutoDao(){
        this.em = JPAUtil.getEntityManager();
    }

    @Override
    public void cadastrar(Produto Produto) throws PersistenceException {
        try{
            this.em.getTransaction().begin();
            this.em.persist(Produto);
            this.em.getTransaction().commit(); // Confirma a transação
        } catch (PersistenceException e) {
            this.em.getTransaction().rollback();
            throw e;
        } finally {
            this.em.close();
        }
    }

    @Override
    public void atualizar(Produto produto) throws PersistenceException {
        try{
            this.em.getTransaction().begin();
            this.em.merge(produto);

           /* String jpql = "UPDATE Produto p SET p.nomeProduto = :nome, p.valorProduto = :valor WHERE p.id = :idProd";
            em.createQuery(jpql, Produto.class)
                    .setParameter("nome", produto.getNomeProduto())
                    .setParameter("valor", produto.getValorProduto())
                    .setParameter("idProd", produto.getId())
                    .executeUpdate();*/

            this.em.getTransaction().commit(); // Confirma a transação
        } catch (PersistenceException e) {
            this.em.getTransaction().rollback();
            throw e;
        } finally {
            this.em.close();
        }
    }

    @Override
    public void remover(Produto produto) throws PersistenceException {
        try {
            this.em.getTransaction().begin(); // Inicie a transação

            // Certifique-se de que a coleção produtos seja inicializada
            produto = em.merge(produto);
            produto.getDoacoes().size(); // Isso carrega a coleção produtos ansiosamente

            for (DoacaoProduto dp : produto.getDoacoes()) {
                em.remove(dp);
            }

            em.remove(produto);

            this.em.getTransaction().commit(); // Confirme a transação
        } catch (PersistenceException e) {
            this.em.getTransaction().rollback(); // Faça rollback em caso de exceção
            throw e;
        } finally {
            this.em.close();
        }
    }

    @Override
    public Produto buscar(Long id) throws PersistenceException {
        Produto produto = new Produto();
        try{
            this.em.getTransaction().begin();

            produto = em.find(Produto.class, id);

            this.em.getTransaction().commit(); // Confirma a transação
        } catch (PersistenceException e) {
            this.em.getTransaction().rollback();
            throw e;
        } finally {
            this.em.close();
        }

        return produto;
    }

    @Override
    public List<Produto> buscarTodos() throws PersistenceException {
        List<Produto> produtos = new ArrayList<>();
        try{
            String jpql = "SELECT p FROM Produto p";
            produtos = this.em.createQuery(jpql, Produto.class).getResultList();
        } catch (PersistenceException e) {
            throw e;
        } finally {
            this.em.close();
        }

        return produtos;
    }

    public List<Produto> buscarTodosGroupBy() throws PersistenceException {
        List<Produto> produtos = new ArrayList<>();
        try{
            String jpql = "SELECT DISTINCT p FROM Produto p LEFT JOIN FETCH p.doacoes";
            produtos = this.em.createQuery(jpql, Produto.class).getResultList();
        } catch (PersistenceException e) {
            throw e;
        } finally {
            this.em.close();
        }

        return produtos;
    }


    public List<Produto> buscarPorNomeProduto(String nomeProduto){
        List<Produto> produtos = new ArrayList<>();

        try{
            String jpql = "SELECT p FROM Produto p WHERE p.nomeProduto LIKE :nomeProduto";
            produtos = em.createQuery(jpql, Produto.class)
                    .setParameter("nomeProduto", nomeProduto)
                    .getResultList();

        } catch (PersistenceException e) {
            throw e;
        } finally {
            this.em.close();
        }

        return produtos;
    }

}
