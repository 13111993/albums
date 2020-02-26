package service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import beans.Album;
import beans.Utilisateur;
import beans.Image;
import util.IOUtilsCust;



public class ImageService extends JpaService<Long,Image> {

    private static final long serialVersionUID = 8759786961691423964L;
    @Inject
    private AlbumDao albumDao;

    // @BUG from tomee. With edit-Image.xhtml, this method is executed
    // even if this.setField(p) is commented in edit(Image) methodd
    // Probably fixed, method JpaDao.java/edit{} must extend JpaDao.java/upload{} and not JpaDao.java/create, but it's stranged that a private method is accessed from another class
    private void setField(Image p) throws DaoException {
        // Or with File.io (older) see for example http://javabydeveloper.com/save-image-working-large-objects/
        // Image size must be smaller than 2 GB with Files.readAllBytes(Path)
        if (p.getPart() != null && p.getPart().getSize()>0) {
            byte[] picBytes;
            try {
                // if Files.size < 0.9 MB
                // By Default MySql restrict file > 1MB
                // http://www.codejava.net/coding/upload-files-to-database-servlet-jsp-mysql
                if (p.getPart().getSize() > 900000) {
                    throw new DaoException("Not allowed to save file > 1MB in Database");
                }
                InputStream partInputStream = p.getPart().getInputStream();
                picBytes = IOUtilsCust.toByteArray(partInputStream);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new DaoException("IOException with upload of file " + p.getPart().getName());
            }
            p.setFile(picBytes);
            // See also http://www.ramkitech.com/2013/06/file-upload-is-easy-in-jsf22.html
            p.setContentType(p.getPart().getHeader("Content-Type"));
        }
    }

    @Override
    public void create(Image p) throws DaoException {
        this.setField(p);
        p.setAlbum(albumDao.read(p.getAlbumId()));
        p.setDateCreated();
        super.create(p);
    }
    @Override
    public void edit(Image p) throws DaoException {
        if (p.getPart() != null && p.getPart().getSize() > 0) {
            this.setField(p);
        }
        p.setAlbum(albumDao.read(p.getAlbumId()));
        super.edit(p);
    }

    public List<Image> listImageOwnedBy(Utilisateur a) throws DaoException {
        //La requete est définie dans la classe Image grâce à une annotation
        Query query = getEm().createNamedQuery("Image.findAllOwned");
        query.setParameter("owner", getEm().merge(a));
        return query.getResultList();
    }

    public List<Image> listImageSharedWith(Utilisateur a) throws DaoException {
        //La requete est définie dans la classe Image grâce à une annotation
        Query query = getEm().createNamedQuery("Image.findAllShared");
        query.setParameter("currentUser", a.getId());
        return query.getResultList();
    }

    public List<Image> listImageFromOneAlbum(Album a) throws DaoException {
        //La requete est définie dans la classe Image grâce à une annotation
        Query query = getEm().createNamedQuery("Image.findAllFromOneAlbum");
        query.setParameter("album", getEm().merge(a.getId()));
        return query.getResultList();
    }

}

// vim: sw=4 ts=4 et:

