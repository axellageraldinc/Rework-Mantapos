package project.blibli.mantapos.NewInterfaceDao;

import project.blibli.mantapos.NewImplementationDao.BasicDao;

public interface MenuYangDipesanDao extends BasicDao<Void, Integer, Integer, Integer, Integer> {
    void insertMenuYangDipesan(int idOrder, int idMenu, int qty);
}