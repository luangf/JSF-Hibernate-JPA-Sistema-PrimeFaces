package managedBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.tomcat.util.codec.binary.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.google.gson.Gson;

import dao.DaoEmail;
import dao.DaoUsuario;
import datatablelazy.LazyDataTableModelUserPessoa;
import model.EmailUser;
import model.UsuarioPessoa;

@ViewScoped
@ManagedBean(name = "usuarioPessoaManagedBean")
public class UsuarioPessoaManagedBean {

	private UsuarioPessoa usuarioPessoa = new UsuarioPessoa();
	private LazyDataTableModelUserPessoa<UsuarioPessoa> list = new LazyDataTableModelUserPessoa<UsuarioPessoa>();
	private DaoUsuario<UsuarioPessoa> daoGeneric = new DaoUsuario<UsuarioPessoa>();
	private BarChartModel barChartModel = new BarChartModel();
	private EmailUser emailUser = new EmailUser();
	private DaoEmail<EmailUser> daoEmail = new DaoEmail<EmailUser>();
	private String campoPesquisa;

	@PostConstruct
	public void init() {
		list.load(0, 5, null, null);
		montarGrafico();
	}

	public void download() throws IOException {
		Map<String, String> params=FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String fileDownloadId=params.get("fileDownloadId");
		
		UsuarioPessoa pessoa=daoGeneric.pesquisar(Long.parseLong(fileDownloadId), UsuarioPessoa.class);
		
		byte[] imagem=new Base64().decodeBase64(pessoa.getImagem().split("\\,")[1]);
		
		HttpServletResponse response=(HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.addHeader("Content-Disposition", "attachment; filename=download.png");
		response.setContentType("application/octet-stream");
		response.setContentLength(imagem.length);
		response.getOutputStream().write(imagem);
		response.getOutputStream().flush();
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	public void upload(FileUploadEvent image) {
		//converter imagem para base 64
		String imagem="data:image/png;base64,"+DatatypeConverter.printBase64Binary(image.getFile().getContent()); //cabeçalho para base 64
		usuarioPessoa.setImagem(imagem);
	}
	
	public void montarGrafico() {
		ChartSeries userSalario = new ChartSeries("Salário dos Usuários");

		for (UsuarioPessoa usuarioPessoa : list.list) {
			userSalario.set(usuarioPessoa.getNome(), usuarioPessoa.getSalario());
		}

		barChartModel.setTitle("Gráfico de Salários");
		barChartModel.addSeries(userSalario);
	}
	
	public void pesquisar() {
		list.pesquisa(campoPesquisa);
		montarGrafico();
	}

	public void excluirEmail() throws Exception {
		String idEmail = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idEmail");

		EmailUser remover = new EmailUser();
		remover.setId(Long.parseLong(idEmail));

		daoEmail.deletarPorId(remover);
		usuarioPessoa.getEmails().remove(remover);

		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Resultado", "Email removido com sucesso!"));
	}

	public void addEmail() {
		emailUser.setUsuarioPessoa(usuarioPessoa);
		emailUser = daoEmail.updateMerge(emailUser); // pega a PK, completando o obj
		usuarioPessoa.getEmails().add(emailUser);
		emailUser = new EmailUser();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Resultado", "Email salvo com sucesso!"));
	}

	public void pesquisaCep(AjaxBehaviorEvent event) {
		try {
			URL url = new URL("https://viacep.com.br/ws/" + usuarioPessoa.getCep() + "/json/");
			URLConnection connection = url.openConnection(); // consumo
			InputStream is = connection.getInputStream(); // demora mais pq entra la na internet...
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = ""; // linha do JSON
			StringBuilder jsonCep = new StringBuilder(); // string final com os dados

			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}

			// Objeto Pessoa aux
			UsuarioPessoa gsonPessoaAux = new Gson().fromJson(jsonCep.toString(), UsuarioPessoa.class);

			usuarioPessoa.setCep(gsonPessoaAux.getCep()); // n precisa pq ja tinha setado, mas por precaução...
			usuarioPessoa.setLogradouro(gsonPessoaAux.getLogradouro());
			usuarioPessoa.setComplemento(gsonPessoaAux.getComplemento());
			usuarioPessoa.setBairro(gsonPessoaAux.getBairro());
			usuarioPessoa.setLocalidade(gsonPessoaAux.getLocalidade());
			usuarioPessoa.setUf(gsonPessoaAux.getUf());
			usuarioPessoa.setIbge(gsonPessoaAux.getIbge());
			usuarioPessoa.setGia(gsonPessoaAux.getGia());
			usuarioPessoa.setDdd(gsonPessoaAux.getDdd());
			usuarioPessoa.setSiafi(gsonPessoaAux.getSiafi());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String salvar() {
		daoGeneric.salvar(usuarioPessoa);
		list.list.add(usuarioPessoa);
		usuarioPessoa = new UsuarioPessoa();
		init();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Salvo com sucesso"));
		return "";
	}

	public String novo() {
		usuarioPessoa = new UsuarioPessoa();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Cadastrando novo usuário"));
		return "";
	}

	public String excluir() {
		try {
			daoGeneric.removerUsuario(usuarioPessoa);
			list.list.remove(usuarioPessoa);
			usuarioPessoa = new UsuarioPessoa();
			init();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Excluido com sucesso"));
		} catch (Exception e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Informação:", "Existem telefones desse usuário"));
			} else {
				e.printStackTrace();
			}
		}
		return "";
	}

	public BarChartModel getBarChartModel() {
		return barChartModel;
	}

	public UsuarioPessoa getUsuarioPessoa() {
		return usuarioPessoa;
	}

	public void setUsuarioPessoa(UsuarioPessoa usuarioPessoa) {
		this.usuarioPessoa = usuarioPessoa;
	}

	public LazyDataTableModelUserPessoa<UsuarioPessoa> getList() {
		montarGrafico();
		return list;
	}

	public void setEmailUser(EmailUser emailUser) {
		this.emailUser = emailUser;
	}

	public EmailUser getEmailUser() {
		return emailUser;
	}

	public String getCampoPesquisa() {
		return campoPesquisa;
	}

	public void setCampoPesquisa(String campoPesquisa) {
		this.campoPesquisa = campoPesquisa;
	}

}
