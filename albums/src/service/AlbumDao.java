package service;

import java.util.HashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import control.UtilisateurSession;
import beans.Album;
import beans.Utilisateur;



public class AlbumDao extends JpaService<Long,Album> {

	private static final long serialVersionUID = -2061392070773108977L;
	@Inject
	private UtilisateurService utilisateurService;
	@Inject
	private UtilisateurSession utilisateurSession;

	private void setSharedWithField(Album a) throws DaoException {
		if (a.getSharedWithArray() != null)
			for (int i=0; i<a.getSharedWithArray().length ; i++) {
				Utilisateur user = utilisateurService.read(a.getSharedWithArray()[i]);
				if (!user.equals(utilisateurSession.getConnectedUser()))
					a.getSharedWith().add(user);
			}
	}

	@Override
	public void create(Album a) throws DaoException {
		a.setSharedWith(new HashSet<Utilisateur>());
		this.setSharedWithField(a);
		a.setOwner(getEm().merge( a.getOwner()));
		a.setDateCreated();
		super.create(a);
	}

	@Override
	public void edit(Album a) throws DaoException {
		this.setSharedWithField(a);
		for (int i=0; i<a.getNoSharedWithArray().length ; i++){
			Utilisateur user = utilisateurService.read(a.getNoSharedWithArray()[i]);
			a.getSharedWith().remove(user);
		}
		super.edit(a);
	}

	public List<Album> listAlbumOwnedBy(Utilisateur a) throws DaoException {
		//La requete est définie dans la classe Album grâce à une annotation
		Query query = getEm().createNamedQuery("Album.findAllOwned");
		query.setParameter("owner", getEm().merge(a));
		return query.getResultList();
	}

	public List<Album> listAlbumShared(Utilisateur a) throws DaoException {
		//La requete est définie dans la classe Album grâce à une annotation
		Query query = getEm().createNamedQuery("Album.findAllShared");
		query.setParameter("currentUser", a.getId());
		return query.getResultList();
	}

}

// vim: sw=4 ts=4 noet:
