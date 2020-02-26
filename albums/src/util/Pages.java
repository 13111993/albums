package util;

public interface Pages {

	// For implementation of interface Filter, always add extension .jsf, even if it isn't
	// mandatory for jsf. Also, do not put « / ».

	String add_album = "add-album.jsf";
	String list_album_owned = "list-album-owned.jsf";
	String add_user = "add-user.jsf";
	String list_user = "list-user.jsf";
	String login = "login.jsf";
	String logout = "logout.jsf";
	String list_picture_owned = "list-picture-owned.jsf";
	String list_album_shared = "list-album-shared.jsf";
	String add_picture = "add-picture.jsf";
	String add_picture_in_spec_album = "add-picture-in-specific-album.jsf";
	String information_current_user = "information-current-user.jsf";
	String edit_user = "edit-user.jsf";
	String edit_album = "edit-album.jsf";
	String error_403 = "error-403.jsf";
	String error_404 = "error-404.jsf";
	String edit_picture = "edit-picture.jsf";
	String list_picture_shared = "list-picture-shared.jsf";
	String list_picture_by_album = "list-picture-by-album.jsf";

	String[] FILTEREDPAGE_CONNECTED = {
		add_album,
		list_album_owned,
		list_picture_owned,
		list_album_shared,
		add_picture,
		add_picture_in_spec_album,
		information_current_user,
		edit_user,
		edit_album,
		edit_picture,
		list_picture_shared,
		list_picture_by_album
	};

}

// vim: sw=4 ts=4 noet:

