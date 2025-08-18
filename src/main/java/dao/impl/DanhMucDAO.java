package dao.impl;

import dao.base.BaseDAO;
import dao.interfaces.IDanhMucDAO;
import model.DanhMuc;

/**
 * DanhMuc DAO implementation extending BaseDAO
 */
public class DanhMucDAO extends BaseDAO<DanhMuc, Integer> implements IDanhMucDAO {
    
    public DanhMucDAO() {
        super(DanhMuc.class);
    }

    @Override
    public void insert(DanhMuc dm) {
        super.insert(dm);
    }

    @Override
    public void update(DanhMuc dm) {
        super.update(dm);
    }

    @Override
    public void delete(int maDM) {
        super.delete(maDM);
    }

    @Override
    public DanhMuc findById(int maDM) {
        return super.findById(maDM).orElse(null);
    }
    
    @Override
    protected void validateEntity(DanhMuc entity) {
        super.validateEntity(entity);
        
        if (entity.getTenDM() == null || entity.getTenDM().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên danh mục không được để trống");
        }
    }
}