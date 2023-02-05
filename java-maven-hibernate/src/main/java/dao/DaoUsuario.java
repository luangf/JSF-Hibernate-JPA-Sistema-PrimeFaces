package dao;

import model.UsuarioPessoa;

public class DaoUsuario<E> extends DaoGeneric<UsuarioPessoa>{

	public void removerUsuario(UsuarioPessoa pessoa) throws Exception {
		getEntityManager().getTransaction().begin();
		
		String sqlDeleteFones="delete from telefoneuser where usuariopessoa_id="+pessoa.getId();
		getEntityManager().createNativeQuery(sqlDeleteFones).executeUpdate();
		
		getEntityManager().getTransaction().commit();
		
		super.deletarPorId(pessoa);
	}

}