package service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;;

@TransactionManagement(TransactionManagementType.CONTAINER)
public abstract class JpaService<K,V> implements Serializable {

	private static final long serialVersionUID = -4220123326848708491L;

	private Class<V> cls;

	@PersistenceContext(unitName = "EssaiJPA",
			type = PersistenceContextType.TRANSACTION)
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public JpaService() {
		cls= (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}

	protected EntityManager getEm() {
		return em;
	}

	public void create(V v) throws DaoException {
		em.persist(v);
	}

	public void edit(V v) throws DaoException {
		this.update(v);
	}

	public V read(K id) throws DaoException {
		return em.find(cls, id);
	}

	public V update(V v) throws DaoException {
		return em.merge(v);
	}

	public void delete(V v) throws DaoException {
		em.remove(v);
	}

	public void deleteById(K id) throws DaoException {
		// getReference (contrairement à find) permet de charger seulement l'id
		// et non toutes les données de l'objet
		em.remove(em.getReference(cls, id));
	}

}

// vim: sw=4 ts=4 noet:
