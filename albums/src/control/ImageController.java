package control;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import beans.Image;
import service.AlbumDao;
import service.ImageService;
import service.DaoException;
import util.Pages;

@Named
@RequestScoped
public class ImageController {

    @Inject
    private UtilisateurSession UtilisateurSession;
    @Inject
    private ImageService ImageService;
    @Inject
    private AlbumDao albumService;
    private Image Image;

    public Image getImage() {
        // useful when launch view add-Image.xhtml
        if (Image == null) {
            Image = new Image();
        }
        return Image;
    }

    public void setImage(Image Image) {
        this.Image = Image;
    }

    private boolean isAllowedModify() {
        if (UtilisateurSession.getConnectedUser().isAdmin()
                || Image.getAlbum().getOwner().equals(UtilisateurSession.getConnectedUser()))
            return true;
        return false;
    }

    public String delete(long ImageId) {
        try {
            Image = ImageService.read(ImageId);
        } catch (DaoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (Image == null) {
            throw new NullPointerException("The album with id " + ImageId + " that you want to destroy not exists in database");
        }
        if (!this.isAllowedModify())
            return Pages.error_403;
        try {
            ImageService.deleteById(ImageId);
        } catch (DaoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Pages.list_picture_owned;
    }

    public String create() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String idString = ctx.getExternalContext().
                getRequestParameterMap().get("albumId");
        Long albumIdRetrieveFromView;
        if (idString != null && !idString.isEmpty()) {
            try {
                albumIdRetrieveFromView = Long.valueOf(idString);
                this.getImage().setAlbum(albumService.read(albumIdRetrieveFromView));
            }
            catch (NumberFormatException e) {
                // TODO
                throw new RuntimeException("You have passed wrong parameters");
            } catch (DaoException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            ImageService.create(Image);
        } catch (DaoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (idString == null || idString.equals(""))
            return Pages.list_picture_owned+"?faces-redirect=true";
        return Pages.list_picture_by_album + "?faces-redirect=true&albumId="
            + idString;
    }

    public String edit() {
        // tests
        Image ImageTmp = Image;
        FacesContext ctx = FacesContext.getCurrentInstance();
        long ImageId;
        try {
        ImageId = Long.valueOf(ctx.getExternalContext().
                getRequestParameterMap().get("ImageId"));
        }
        catch (NumberFormatException e) {
            // TODO
            throw new RuntimeException("You have passed wrong parameters");
        }
        long albumId;
        try {
        albumId = Long.valueOf(ctx.getExternalContext().
                getRequestParameterMap().get("albumId"));
        }
        catch (NumberFormatException e) {
            // TODO
            throw new RuntimeException("You have passed wrong parameters");
        }
        String fromPage = ctx.getExternalContext().
                getRequestParameterMap().get("fromPage");
        // TODO not RuntimeException, but info
        if (!fromPage.equals(Pages.list_picture_owned)
                && !fromPage.equals(Pages.list_picture_by_album))
                throw new IllegalArgumentException("You have passed wrong parameters");
        try {
            this.Image = ImageService.read(ImageId);
        } catch (DaoException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (!this.isAllowedModify())
            return Pages.error_403;
        // ** perform modifications
        Image.setAlbumId(ImageTmp.getAlbumId());
        Image.setTitle(ImageTmp.getTitle());
        if (ImageTmp.getPart()!=null && ImageTmp.getPart().getSize()>0){
            Image.setPart(ImageTmp.getPart());
        }
        try {
            ImageService.edit(Image);
        } catch (DaoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return fromPage+"?faces-redirect=true&albumId="+albumId;
    }

    public List<Image> getListImageOwnedByCurrentUser() {
        try {
            return ImageService.listImageOwnedBy(UtilisateurSession.getConnectedUser());
        } catch (DaoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public List<Image> getListImageSharedWithCurrentUser() {
        try {
            return ImageService.listImageSharedWith(UtilisateurSession.getConnectedUser());
        } catch (DaoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}



