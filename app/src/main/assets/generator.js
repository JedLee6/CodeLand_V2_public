Blockly.JavaScript['one'] = function(block) {
  // Generate JavaScript for turning left or right.
  //var value = block.getFieldValue('VALUE');
  //return 'Turtle.' + block.getFieldValue('DIR') +
    //  '(' + value + ', \'block_id_' + block.id + '\');\n';
   return '=one';
};
Blockly.JavaScript['一'] = function(block) {
  // Generate JavaScript for turning left or right.
  //var value = block.getFieldValue('VALUE');
  //return 'Turtle.' + block.getFieldValue('DIR') +
    //  '(' + value + ', \'block_id_' + block.id + '\');\n';
   return '一';
};


Blockly.JavaScript['apple_chinese'] = function(block) {
  return '苹果=';
};
Blockly.JavaScript['apple_english'] = function(block) {
  return 'apple';
};


Blockly.JavaScript['banana_chinese'] = function(block) {
  return '香蕉=';
};
Blockly.JavaScript['banana_english'] = function(block) {
  return 'banana';
};


Blockly.JavaScript['orange_chinese'] = function(block) {
  return '橘子=';
};
Blockly.JavaScript['orange_english'] = function(block) {
  return 'orange';
};

//诗歌部分的生成器函数
Blockly.JavaScript['poetry_contents'] = function(block) {
    return block.getFieldValue("CONTENT");
};
Blockly.JavaScript['poetry_title'] = function(block) {
    return block.getFieldValue("TITLE");
};

Blockly.JavaScript['poetry'] = function(block) {
    var title = Blockly.JavaScript.statementToCode(block, 'TITLE').substring(2);
    var content = Blockly.JavaScript.statementToCode(block, 'CONTENT').substring(2);

    return 'poetry='+title+'='+content;
};

Blockly.JavaScript['poetry_first'] = function(block) {
    return block.getFieldValue("CONTENT")+'=';
};

Blockly.JavaScript['poetry_second'] = function(block) {
    return block.getFieldValue("CONTENT");
};

//三字经部分的生成器函数

Blockly.JavaScript['sanzijing_contents'] = function(block) {
    return block.getFieldValue("CONTENT");
};
Blockly.JavaScript['sanzijing_title'] = function(block) {
    return block.getFieldValue("TITLE");
};

Blockly.JavaScript['sanzijing'] = function(block) {
    var title = Blockly.JavaScript.statementToCode(block, 'TITLE').substring(2);
    var content = Blockly.JavaScript.statementToCode(block, 'CONTENT').substring(2);

    return 'sanzijing='+title+'='+content;
};

Blockly.JavaScript['sanzijing_first'] = function(block) {
    return block.getFieldValue("CONTENT")+'=';
};

Blockly.JavaScript['sanzijing_second'] = function(block) {
    return block.getFieldValue("CONTENT");
};


//名人事迹部分的生成器函数
Blockly.JavaScript['celebrity_contents'] = function(block) {
    return block.getFieldValue("CONTENT");
};
Blockly.JavaScript['celebrity_title'] = function(block) {
    return block.getFieldValue("TITLE");
};

Blockly.JavaScript['celebrity'] = function(block) {
    var title = Blockly.JavaScript.statementToCode(block, 'TITLE').substring(2);
    var content = Blockly.JavaScript.statementToCode(block, 'CONTENT').substring(2);

    return 'celebrity='+title+'='+content;
};

Blockly.JavaScript['celebrity_first'] = function(block) {
    return block.getFieldValue("CONTENT")+'=';
};

Blockly.JavaScript['celebrity_second'] = function(block) {
    return block.getFieldValue("CONTENT");
};




Blockly.JavaScript['animal_name'] = function(block) {
  var text_aname = block.getFieldValue('ANAME');
  return text_aname;
};

Blockly.JavaScript['animal_eating'] = function(block) {
  var text_aeating = block.getFieldValue('AEATING');
  return text_aeating;
};

Blockly.JavaScript['animal'] = function(block) {
  var value_aname = Blockly.JavaScript.statementToCode(block, 'aName').substring(2);
  var value_eating = Blockly.JavaScript.statementToCode(block, 'eating').substring(2);
  // TODO: Assemble JavaScript into code variable.
  var code =""+value_aname+"="+value_eating;
  return code;
};

Blockly.JavaScript['animal_instance'] = function(block) {
  var text_instance = block.getFieldValue('INSTANCE');
  return "-"+text_instance;
};

Blockly.JavaScript['animal_kind'] = function(block) {
  var text_kind = block.getFieldValue('KIND');
  var statements_include = Blockly.JavaScript.statementToCode(block, 'INCLUDE').substring(2);
  return text_kind+"="+statements_include;
};