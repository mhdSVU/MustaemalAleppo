package mohammedyouser.com.mustaemalaleppo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import mohammedyouser.com.mustaemalaleppo.Domain.Item;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<String[]> selectedItem = new MutableLiveData<>();
    public void selectItem(String[] item) {
        selectedItem.setValue(new String[]{"",""});
    }
    public LiveData<String[]> getSelectedItem() {
        return selectedItem;
    }
}