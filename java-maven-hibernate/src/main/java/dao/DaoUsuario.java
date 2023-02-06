package dao;

import java.util.List;

import javax.persistence.Query;

import model.UsuarioPessoa;

public class DaoUsuario<E> extends DaoGeneric<UsuarioPessoa>{

	public void removerUsuario(UsuarioPessoa pessoa) throws Exception {
		getEntityManager().getTransaction().begin();
		
		getEntityManager().remove(pessoa);
		
		getEntityManager().getTransaction().commit();
	}

	public List<UsuarioPessoa> pesquisar(String campoPesquisa) {
		Query query=super.getEntityManager().createQuery("from UsuarioPessoa where upper(nome) like '%"+campoPesquisa.trim().toUpperCase()+"%'");
		return query.getResultList();
	}

}