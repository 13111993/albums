package service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import beans.Utilisateur;


@TransactionManagement(TransactionManagementType.CONTAINER)
public class UtilisateurService extends JpaService<Long,Utilisateur> implements Serializable {

	private static final long serialVersionUID = 3975934852005119859L;

	private void setField(Utilisateur v) throws DaoException {
		try {
			super.create(v);
		}
		catch (RollbackException e) {
			if (e.getCause() instanceof EntityExistsException) {
				throw new DaoException("Un utilisateur avec l'email "+v.getEmail()+" existe déjà",e);
			}
			else {
				new DaoException(e);
			}
		}
	}

	@Override
	public void create(Utilisateur v) throws DaoException {
		v.setDateCreated();
		this.setField(v);
		super.create(v);
	}

	@Override
	public void edit(Utilisateur v) throws DaoException {
		this.setField(v);
		super.edit(v);
	}


	public Utilisateur login(String email, String password) throws DaoException {
		Query query = getEm().createNamedQuery("Utilisateur.login");
		query.setParameter("email", email);
		query.setParameter("password", password);
		try {
			return (Utilisateur) query.getSingleResult();
		}
		catch (NoResultException e) {
			throw new DaoException("Utilisateur Inconnu",e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Utilisateur> listUsers() throws DaoException {
		Query query = getEm().createNamedQuery("AppUser.findAll");
		return query.getResultList();
	}
}

// vim: sw=4 ts=4 noet:

