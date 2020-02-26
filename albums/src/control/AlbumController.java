package control;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import beans.Album;
import beans.Utilisateur;
import beans.Image;
import service.AlbumDao;
import service.UtilisateurService;
import service.DaoException;
import util.Pages;

@Named
@ConversationScoped


public class AlbumController implements Serializable {

	private static final long serialVersionUID = 2729195466703888571L;
	@Inject
	private UtilisateurSession UtilisateurSession;
	@Inject
	private AlbumDao albumService;
	@Inject
	private UtilisateurService UtilisateurService;
	@Inject
	private Conversation conversation;

	private Album album;

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	public void initConversation(){
		conversation.begin();
	}

	public String endConversation(){
		conversation.end();
		// Mandotory, otherwise append "?cid=[nubmer]"
		return "?faces-redirect=true";
	}

	public String viewImageByAlbum(Long albumIdRetrieveFromView) {
		try {
			this.album = albumService.read(albumIdRetrieveFromView);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Pages.list_picture_by_album;
	}

	public Album getAlbum() {
		// useful when launch view add-album.xhtml
		if (album==null) {
			album = new Album(UtilisateurSession.getConnectedUser());
		}
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public String blop() {
		return Pages.login;
	}

	private boolean isAllowedModify() {
		if (UtilisateurSession.getConnectedUser().isAdmin()
				|| album.getOwner().equals(UtilisateurSession.getConnectedUser()))
			return true;
		return false;
	}

	public String create() {
		if (!this.isAllowedModify())
			return Pages.error_403;
		try {
			albumService.create(album);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Pages.list_album_owned;
	}

	public String edit() {
		if (!this.isAllowedModify())
			return Pages.error_403;
		try {
			albumService.edit(album);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Pages.list_album_owned + this.endConversation();
	}

	public String delete(long albumId) {
		try {
			album = albumService.read(albumId);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (album == null) {
			// TODO : give message
			throw new NullPointerException("The album with id " + albumId + " that you want to destroy not exists in database");
		}
		if (!isAllowedModify())
			return Pages.error_403;
		try {
			albumService.deleteById(albumId);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Pages.list_album_owned;
	}

	public List<Album> getListAlbumOwnedByCurrentUser() {
		try {
			return albumService.listAlbumOwnedBy(UtilisateurSession.getConnectedUser());
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<Album> getListAlbumShared() {
		try {
			return albumService.listAlbumShared(UtilisateurSession.getConnectedUser());
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Set<Utilisateur> getNoSharedWith(){
		Set<Utilisateur> userList = new HashSet<Utilisateur>();
		try {
			userList = new HashSet<Utilisateur>(UtilisateurService.listUsers());
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userList.removeAll(album.getSharedWith());
		return userList;
	}

	
	}



// vim: sw=4 ts=4 noet:

