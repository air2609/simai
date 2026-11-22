<template>
  <div>
    <div class="page-header">
      <h2>Lokasi</h2>
      <el-button v-if="canEdit" type="primary" @click="openCreate">
        Tambah Lokasi
      </el-button>
    </div>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe row-key="id">
        <el-table-column prop="code" label="Kode" width="120" />
        <el-table-column prop="name" label="Nama" min-width="180" />
        <el-table-column prop="parentName" label="Induk" min-width="150">
          <template #default="{ row }">
            {{ row.parentName || '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="address" label="Alamat" min-width="200" show-overflow-tooltip />
        <el-table-column v-if="canEdit" label="Aksi" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">Edit</el-button>
            <el-button size="small" type="danger" @click="remove(row)">Hapus</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? 'Edit Lokasi' : 'Tambah Lokasi'"
      width="520px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="Kode" prop="code">
          <el-input v-model="form.code" placeholder="mis. WH-01" />
        </el-form-item>
        <el-form-item label="Nama" prop="name">
          <el-input v-model="form.name" placeholder="mis. Gudang Utama" />
        </el-form-item>
        <el-form-item label="Induk" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            check-strictly
            clearable
            placeholder="Pilih lokasi induk (opsional)"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="Alamat" prop="address">
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="Alamat (opsional)" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">Batal</el-button>
        <el-button type="primary" :loading="saving" @click="save">Simpan</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import {
  createLocation,
  deleteLocation,
  getLocations,
  updateLocation
} from '../../api/modules/master'

const auth = useAuthStore()
const canEdit = computed(() => auth.user?.role === 'ADMIN')

const rows = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref()

const emptyForm = () => ({ code: '', name: '', parentId: null, address: '' })
const form = reactive(emptyForm())

const rules = {
  code: [{ required: true, message: 'Kode wajib diisi', trigger: 'blur' }],
  name: [{ required: true, message: 'Nama wajib diisi', trigger: 'blur' }]
}

const treeData = computed(() => buildTree(rows.value))

function buildTree(list) {
  const map = {}
  list.forEach((l) => {
    map[l.id] = { ...l, children: [] }
  })
  const roots = []
  list.forEach((l) => {
    if (l.parentId && map[l.parentId]) {
      map[l.parentId].children.push(map[l.id])
    } else {
      roots.push(map[l.id])
    }
  })
  return roots
}

async function load() {
  loading.value = true
  try {
    rows.value = await getLocations()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  isEdit.value = false
  editId.value = null
  Object.assign(form, emptyForm())
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    code: row.code,
    name: row.name,
    parentId: row.parentId,
    address: row.address || ''
  })
  dialogVisible.value = true
}

async function save() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = { ...form, parentId: form.parentId || null }
    if (isEdit.value) {
      await updateLocation(editId.value, payload)
      ElMessage.success('Lokasi berhasil diperbarui')
    } else {
      await createLocation(payload)
      ElMessage.success('Lokasi berhasil dibuat')
    }
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`Hapus lokasi "${row.name}"?`, 'Konfirmasi', { type: 'warning' })
  } catch {
    return
  }
  await deleteLocation(row.id)
  ElMessage.success('Lokasi berhasil dihapus')
  await load()
}

onMounted(load)
</script>
