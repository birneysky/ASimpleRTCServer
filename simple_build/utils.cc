#include "utils.h"

std::string ToString(const int s) {
  char buf[32];
  const int len = std::snprintf(&buf[0], 32, "%d", s);
  return std::string(&buf[0], len);
}

std::string int2str(int i) {
  return ToString(i);
}

std::string size_t2str(size_t i) {
  return ToString(i);
}