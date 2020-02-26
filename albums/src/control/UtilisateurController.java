package control;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import beans.Utilisateur;
import service.UtilisateurService;
import service.DaoException;
import util.Pages;


/*
 * L'annotation @Named permet de créer un bean CDI
 * Le bean CDI va remplacer les ManagedBean de JSF à partir de JSF 2.3
 * Leur intéret est qu'ils sont utilisables en dehors du contexte JSF.
 * On peut les utiliser aussi via l'annotation @Inject
 * Il faut faire attention que l'annotation @RequestScoped vienne bien du package
 * javax.enterprise.context et non de l'ancien package javax.faces.bean
 */
@Named
@RequestScoped
public class UtilisateurController {

	@Inject
	private UtilisateurService UtilisateurService;
	@Inject
	private UtilisateurSession UtilisateurSession;

	private Utilisateur user;

	public Utilisateur getUser() {
		if (user == null)
			user = new Utilisateur();
		return user;
	}

	private boolean isAllowedModify() {
		if (UtilisateurSession.getConnectedUser().isAdmin()
				|| UtilisateurSession.getConnectedUser().equals(user))
			return true;
		return false;
	}

	public String create() {
		try {
			user.setAdmin(false);
			UtilisateurService.create(user);
		} catch (DaoException e) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			FacesMessage facesMessage = new FacesMessage(e.getLocalizedMessage());
			facesContext.addMessage("email", facesMessage);
			return null;
		}
		return Pages.list_user;
	}

	public String edit() {
		Utilisateur userTmp = user;
		FacesContext ctx = FacesContext.getCurrentInstance();
		Long userId;
		try {
		userId = Long.valueOf(ctx.getExternalContext().
				getRequestParameterMap().get("userId"));
		}
		catch (NumberFormatException e) {
			// TODO
			throw new RuntimeException("You have passed wrong parameters");
		}
		try {
			user = UtilisateurService.read(userId);
		} catch (DaoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (!this.isAllowedModify())
			return Pages.error_403;
		user.setFirstname(userTmp.getFirstname());
		user.setLastname(userTmp.getLastname());
		if (UtilisateurSession.getConnectedUser().isAdmin())
				user.setAdmin(userTmp.getAdminString().equals("true"));
		user.setPassword(userTmp.getPassword());
		try {
			UtilisateurService.edit(user);
		} catch (DaoException e) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			FacesMessage facesMessage = new FacesMessage(e.getLocalizedMessage());
			facesContext.addMessage("email", facesMessage);
			return null;
		}
		return Pages.list_user;
	}

	public String viewEditPage(Long userIdRetrieveFromView) {
		try {
			this.user = UtilisateurService.read(userIdRetrieveFromView);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Pages.edit_user;
	}

	public String delete(long userId) {
		try {
			UtilisateurService.deleteById(userId);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!this.isAllowedModify())
			return Pages.error_403;
		return Pages.list_user;
	}

	public List<Utilisateur> listUsers() throws DaoException {
		return UtilisateurService.listUsers();
	}

}



