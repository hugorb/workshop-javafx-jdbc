package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	
	private Department department;
	
	private DepartmentService departmentService;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button buttonSave;
	
	@FXML
	private Button buttonCancel;
	
	@FXML
	private void onButtonSaveAction(ActionEvent event) {
		if (department == null) {
			throw new IllegalStateException("Null department");
		}
		if (departmentService == null) {
			throw new IllegalStateException("Null department");
		}
		try {
			department = getFormData();
			departmentService.saveOrUpdate(department);
			Utils.currentStage(event).close();
		}
		catch (DbException e) {
			Alerts.showAlert("Erro saving department", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private Department getFormData() {
		Department department = new Department(Utils.tryParceToInt(txtId.getText()), txtName.getText());
		return department;
	}

	@FXML
	private void onButtonCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void setDepertment(Department department) {
		this.department = department;
	}
	
	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
	
	public void updateFormData() {
		if (department == null) {
			throw new IllegalStateException("Null department");
		}
		txtId.setText(String.valueOf(department.getId()));
		txtName.setText(department.getName());
	}

}
