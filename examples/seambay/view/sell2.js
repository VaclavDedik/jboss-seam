// We put this constant here because facelets would otherwise interpret it as an EL expression
var CATEGORY_EXPR = "#{allCategories}";

var categories;

function loadCategories(result)
{
  categories = result;
  
  var catList = document.getElementById("rootCategory");
  
  catList.options.length = 0;
  
  for (var i = 0; i < categories.length; i++)
  {
    var cat = categories[i];
    
    if (cat.getParent() == null)
    {     
      var option = new Option(cat.getName() + (isParent(cat) ? " >" : ""), cat.getCategoryId());
      var idx = catList.options.length;
      catList.options[idx] = option;
    }
  }   
}

function isParent(category)
{
  if (category.isParent)
    return category.isParent;
    
  for (var i = 0; i < categories.length; i++)
  {
    if (categories[i].getParent() == category)
    {
      category.isParent = true;
      return true;
    }
  } 
  
  category.isParent = false;
  return false;
}

function getSelectedOption(ctl)
{
  for (var i = 0; i < ctl.options.length; i++)
  {
    if (ctl.options[i].selected)
      return ctl.options[i]; 
  }   
  return null;
}

function selectCategory(ctl)
{
  var opt = getSelectedOption(ctl);
  document.getElementById("categoryId").value = opt.value;
}