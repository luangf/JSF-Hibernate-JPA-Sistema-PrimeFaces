package managedBean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import dao.DaoTelefones;
import dao.DaoUsuario;
import model.TelefoneUser;
import model.UsuarioPessoa;

@ViewScoped
@ManagedBean(name = "telefoneManagedBean")
public class TelefoneManagedBean {

	private UsuarioPessoa usuarioPessoa = new UsuarioPessoa();
	private DaoUsuario<UsuarioPessoa> daoGeneric = new DaoUsuario<UsuarioPessoa>();
	private DaoTelefones<TelefoneUser> daoTelefone = new DaoTelefones<TelefoneUser>();
	private TelefoneUser telefoneUser = new TelefoneUser();

	public String excluir() throws Exception {
		daoTelefone.deletarPorId(telefoneUser);
		usuarioPessoa = daoGeneric.pesquisar(usuarioPessoa.getId(), UsuarioPessoa.class);
		telefoneUser = new TelefoneUser();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Excluido com sucesso"));
		return "";
	}
	
	public String salvar() {
		telefoneUser.setUsuarioPessoa(usuarioPessoa);
		daoTelefone.salvar(telefoneUser);
		usuarioPessoa = daoGeneric.pesquisar(usuarioPessoa.getId(), UsuarioPessoa.class);
		telefoneUser = new TelefoneUser();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Salvo com sucesso"));
		return "";
	}

	@PostConstruct
	public void init() {
		String idUser = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idUser");
		usuarioPessoa = daoGeneric.pesquisar(Long.parseLong(idUser), UsuarioPessoa.class);
	}

	public UsuarioPessoa getUsuarioPessoa() {
		return usuarioPessoa;
	}

	public void setUsuarioPessoa(UsuarioPessoa usuarioPessoa) {
		this.usuarioPessoa = usuarioPessoa;
	}

	public TelefoneUser getTelefoneUser() {
		return telefoneUser;
	}

	public void setTelefoneUser(TelefoneUser telefoneUser) {
		this.telefoneUser = telefoneUser;
	}

}
