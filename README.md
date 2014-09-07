ParcelableJsonModel
===========

This model can be extended to be easily serialized to and from Parcel and JSON

Example:

    public class TestModel extends Model {

            @ModelField(json = "id")
            private int             mId;
    
            @ModelField(json = "list")
            private List<String>    mList;
    
            public int getId() {
                return mId;
            }
    
            public TestModel setId(int i) {
                mId = i;
                return this;
            }
    
            public List<String> getList() {
                return mList;
            }
    
            public TestModel setList(List<String> list) {
                mList = list;
                return this;
            }
        }


        TestModel model = new TestModel().setAttributes("{\"id\": 1, \"list\": [\"value1\", \"value2\"]}");
        model.getId();  // = 1
        model.getList(); // = List([value1, value2])
