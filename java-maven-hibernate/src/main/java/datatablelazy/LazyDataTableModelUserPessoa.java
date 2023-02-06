package datatablelazy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import dao.DaoUsuario;
import model.UsuarioPessoa;

public class LazyDataTableModelUserPessoa<T> extends LazyDataModel<UsuarioPessoa>{

	private DaoUsuario<UsuarioPessoa> dao=new DaoUsuario<UsuarioPessoa>();
	public List<UsuarioPessoa> list=new ArrayList<UsuarioPessoa>();
	private String sql=" from UsuarioPessoa ";
	
	@Override
	public List<UsuarioPessoa> load(int first, int pageSize, Map<String, SortMeta> sortBy,
			Map<String, FilterMeta> filterBy) {
		
		list=dao.getEntityManager().createQuery(getSql())
				.setFirstResult(first)
				.setMaxResults(pageSize)
				.getResultList();
		
		sql="from UsuarioPessoa";
		
		setPageSize(pageSize);
		
		Integer qtdRegistros=Integer.parseInt(dao.getEntityManager().createQuery("select count(1) "+getSql()).getSingleResult().toString());
		setRowCount(qtdRegistros);
		
		return list;
		
	}
	
	public void pesquisa(String campoPesquisa) {
		sql+=" where nome like '%"+campoPesquisa+"%'";
	}
	
	public String getSql() {
		return sql;
	}
	
	public List<UsuarioPessoa> getList() {
		return list;
	}

	@Override
	public int count(Map<String, FilterMeta> filterBy) {
		return 0;
	}
	
}
