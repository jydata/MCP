/**
 * Created by zhuangwei on 2018/7/17.
 */
export default class Validate {
  /**
   * 验证必填元素
   */
  static required(value) {
    if (typeof value === 'number') {
      value = value.toString()
    } else if (typeof value === 'boolean') {
      return !0
    }
    return value && value.length > 0
  }

  /**
   * 去除重复
   */
  static noDuplicate(array) {
    return [...new Set(array)]
  }

  /**
   * 验证电子邮箱格式
   */
  static email(value) {
    return /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test(value)
  }

  /**
   * 验证手机格式
   */
  static mobile(value) {
    return /^1[34578]\d{9}$/.test(value)
  }

  /**
   * 验证URL格式
   */
  static url(value) {
    return /^(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,})).?)(?::\d{2,5})?(?:[/?#]\S*)?$/i.test(value)
  }

  /**
   * 验证日期格式
   */
  static date(value) {
    return !/Invalid|NaN/.test(new Date(value).toString())
  }

  /**
   * 验证ISO类型的日期格式
   */
  static dateISO(value) {
    return /^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])$/.test(value)
  }

  /**
   * 验证十进制数字
   */
  static number(value) {
    return /^(?:-?\d+|-?\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(value)
  }

  /**
   * 验证整数
   */
  static digits(value) {
    return /^\d+$/.test(value)
  }

  /**
   * 验证身份证号码
   */
  static idCard(value) {
    return /^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/.test(value)
  }
}
