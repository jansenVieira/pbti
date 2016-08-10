package to;

import java.util.Date;
import util.UtilDate;

public class ConnectionTO {

    private String codPerfil;
    private String desPerfil;
    private Date   fecDesact;
    private String usuarioLogin;

    /**
     * @return the usuarioLogin
     */
    public String getUsuarioLogin() {
        return usuarioLogin;
    }
    /**
     * @param usuarioLogin the usuarioLogin to set
     */
    public void setUsuarioLogin(String loginUsuario) {
        this.usuarioLogin = loginUsuario;
    }
    /**
     * @return the fecDesact
     */
    public String getFecDesact() {
        String result = null;
        if ( fecDesact != null && !fecDesact.equals("") )
            result = UtilDate.formatDate( fecDesact, UtilDate.PATTERN_DATE_CONTROLSA );
        return result;
    }
    /**
     * @param fecDesact the fecDesact to set
     */
    public void setFecDesact(Date fecDesact) {
        this.fecDesact = fecDesact;
    }
    /**
     * @return the codPerfil
     */
    public String getCodPerfil() {
        return codPerfil;
    }
    /**
     * @param codPerfil the codPerfil to set
     */
    public void setCodPerfil(String codPerfil) {
        this.codPerfil = codPerfil;
    }
    /**
     * @return the desPerfil
     */
    public String getDesPerfil() {
        return desPerfil;
    }
    /**
     * @param desPerfil the desPerfil to set
     */
    public void setDesPerfil(String desPerfil) {
        this.desPerfil = desPerfil;
    }
    
}