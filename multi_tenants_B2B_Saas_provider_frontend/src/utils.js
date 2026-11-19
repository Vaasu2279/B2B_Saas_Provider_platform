export const can = (perm) => {
  const a = JSON.parse(localStorage.getItem("authorities") || "[]");
  return a.includes(perm);
};