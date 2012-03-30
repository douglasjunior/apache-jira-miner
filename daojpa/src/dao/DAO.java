package dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Esta classe cotêm operacoes CRUD com JPA.
 * Para que funcione adequadamente você precisa
 * passar no construtor um objeto de EntityManager.
 * A Unidade de Persistencia, as entidades e o driver do banco
 * são definidos no SEU sistema.
 * @author Douglas Nassif Roma Junior
 */
public class DAO {

    private EntityManager em;

    /**
     * Método construtor que recebe um Objeto EntityManager.
     * @param em Objeto do tipo EntityManager.
     */
    public DAO(EntityManager em) {
        this.em = em;
    }

    public DAO() {
    }

    /**
     * Este método insere um objeto utilizando o EntityManager.
     * @param objeto Objeto da entidade do seu sistema.
     */
    public boolean insere(Object objeto) {
        if (!daoValido()) {
            return false;
        }
        try {
            em.getTransaction().begin();
            em.persist(objeto);
            em.flush();
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            try {
                em.getTransaction().rollback();
            } catch (Exception ex) {
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Este método encontre um Objeto no banco a partir do número de ID.
     * @param classe Tipo de Objeto que será retornado.
     * @param id Número no formato Inteiro que representa o ID do objeto.
     * @return Objeto encontrado a partir do parámetro passado no ID.
     */
    public Object buscaID(Class classe, String id) {
        if (!daoValido()) {
            return new Object();
        }
        Long numeroId;
        if (id.isEmpty() || id == null) {
            numeroId = Long.parseLong("0");
        } else {
            numeroId = Long.parseLong(id);
        }
        Object objeto = em.find(classe, numeroId);
        try {
            em.refresh(objeto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return objeto;
    }

    public Object buscaIDint(Class classe, int numeroId) {
        if (!daoValido()) {
            return new Object();
        }
        Object objeto = em.find(classe, numeroId);
        try {
            em.refresh(objeto);
        } catch (Exception ex) {
            
        }
        return objeto;
    }

    /**
     * Este método atualiza atualiza um Objeto no Banco de Dados de acordo com seus parâmetros.
     * @param objeto Objeto a ser atualizado no banco de dados.
     */
    public boolean atualiza(Object objeto) {
        if (!daoValido()) {
            return false;
        }
        try {
            em.getTransaction().begin();
            em.merge(objeto);
            em.flush();
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Este método remove um Objeto cadastrado no banco de dados.
     * @param objeto Objeto que será removido do banco de dados.
     */
    public boolean remove(Object objeto) throws Exception {
        if (!daoValido()) {
            return false;
        }
        try {
            em.getTransaction().begin();
            em.remove(objeto);
            em.getTransaction().commit();
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Este método retorna uma lista de objetos de acordo com o SELECT em SQL passado por parâmetro.
     * @param selectSql Comando SQL contendo o SELECT desejado.
     * @return Lista genérica de objetos encontrados no campo de dados.
     */
    public List seleciona(String selectSql) {
        if (!daoValido()) {
            return new ArrayList();
        }
        List lstResultado = em.createQuery(selectSql).getResultList();
        return lstResultado;
    }

    /**
     * Este método retorna uma lista contendo todos os objetos do Tipo que lhe for indicado.
     * @param classe String contendo o Nome da classe no qual os objetos serão retornados.
     * @param ordernarPor String contendo o Nome do atributo da classe no qual a lista será ordenada.
     * @return Lista Lista genérica de objetos encontrados no campo de dados.
     */
    public List selecionaTodos(String classe, String ordernarPor) {
        if (!daoValido()) {
            return new ArrayList();
        }
        List lstResultado = em.createQuery("SELECT x FROM " + classe + " x ORDER BY x." + ordernarPor).getResultList();
        return lstResultado;
    }

    private boolean daoValido() {
        if (em == null) {
            System.err.println("DAO INVALIDO: erro ao tentar utilizar DAO");
            return false;
        }
        return true;
    }

    /**
     * Este método retorna a EntityManager usada no DAO.
     * @return Objeto do tipo EntityManager.
     */
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Este método executa uma Query baseada em parâmetros.
     * Usado para executar Querys usando atributos Enum.
     * @param select Query completa com parâmetros. Ex: "SELECT c FROM Casa c WHERE c.nome = :nome"
     * @param parametros Vetor de Strings contendo atributos usados na Query. Ex: new String[]{"nome"};
     * @param objetos Vetor de Objetos, que podem ser to tipo Enum. Ex: SituacaoCasa.Ilegal
     * @return Objeto do tipo List contendo todos os Objetos retornados do banco de dados.
     */
    public List selecionaComParametros(String select, String[] parametros, Object[] objetos) {
        Query query = em.createQuery(select);
        if (parametros.length != objetos.length) {
            System.err.println("O numero de parametros difere do numero de atributos.");
            return null;
        }
        for (int i = 0; i < parametros.length; i++) {
            String atributo = parametros[i];
            Object parametro = objetos[i];
            query.setParameter(atributo, parametro);
        }
        return query.getResultList();
    }

    public List consultarNativo(String sql) {
        return em.createNativeQuery(sql).getResultList();
    }

    public void refreshObjeto(Object object) {
        em.refresh(object);
    }

    public List executeNamedQuery(String namedQuery) {
        return em.createNamedQuery(namedQuery).getResultList();
    }

    public List executeNamedQueryComParametros(String namedQuery, String[] parametros, Object[] objetos) {
        Query query = em.createNamedQuery(namedQuery);
        if (parametros.length != objetos.length) {
            System.err.println("A quantidade de parametros difere da quantidade de atributos.");
            return null;
        }
        for (int i = 0; i < parametros.length; i++) {
            String atributo = parametros[i];
            Object parametro = objetos[i];
            query.setParameter(atributo, parametro);
        }
        return query.getResultList();
    }

    public boolean fecharConexao() {
        try {
            em.clear();
            em.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
