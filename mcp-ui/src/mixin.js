var Mixin = {
  methods: {
    // 时间戳转为时间字符串 7/29/19 11:56 AM
    formatDate(row, column) {
      return this._formatDate(row[column.property])
    },
    _formatDate(value) {
      return value == null ? "" : this.$moment(value).format('MM/DD/YY h:mm A');
    },
  }
}
export default Mixin